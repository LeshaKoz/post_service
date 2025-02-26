package faang.school.postservice.service.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.config.props.PostProperties;
import faang.school.postservice.dto.post.PostCreateDto;
import faang.school.postservice.dto.post.PostOwnerType;
import faang.school.postservice.dto.post.PostReadDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.BusinessException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.mapper.PostMapperImpl;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.service.HashtagService;
import faang.school.postservice.service.PaginationService;
import faang.school.postservice.service.s3.S3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private ProjectServiceClient projectServiceClient;
    @Mock
    private HashtagService hashtagService;
    @Mock
    private PostRepository postRepository;
    @Mock
    private UserContext userContext;
    @Spy
    private PaginationService paginationService;
    @Mock
    private ModerationDictionary moderationDictionary;
    @Spy
    private PostMapper postMapper = new PostMapperImpl();
    @Spy
    private PostProperties postProperties;
    @InjectMocks
    private PostService postService;
    @Mock
    private S3Service s3Service;
    @Mock
    private ResourceRepository resourceRepository;
    @Mock
    private PostImageService postImageService;

    private Post post;

    @Captor
    ArgumentCaptor<Post> postArgumentCaptor;

    @BeforeEach
    void setUp() {
        post = Post.builder()
                .content("content")
                .id(1L)
                .build();
    }

    @Test
    void testCreatePostDraftWithAuthorNotExist() {
        long userId = 1;
        when(userServiceClient.getUser(userId)).thenReturn(null);
        var dto = PostCreateDto.builder()
                .content("content")
                .authorId(userId)
                .build();

        assertThrows(EntityNotFoundException.class, () -> postService.createPostDraft(dto));
    }

    @Test
    void testCreatePostDraftWithProjectNotExist() {
        long projectId = 1;
        when(projectServiceClient.getProject(projectId)).thenReturn(null);
        var dto = PostCreateDto.builder()
                .content("content")
                .projectId(projectId)
                .build();

        assertThrows(EntityNotFoundException.class, () -> postService.createPostDraft(dto));
    }

    @Test
    void testCreatePostDraftSuccessCase() {
        long userId = 1;
        when(userServiceClient.getUser(userId))
                .thenReturn(new UserDto(1L, "user", "user@gmail.com"));
        var createDto = PostCreateDto.builder()
                .content("content")
                .authorId(userId)
                .build();

        postService.createPostDraft(createDto);

        verify(postRepository, atLeastOnce()).save(any());
    }

    @Test
    void testCreatePostDraftWithNonExistingHashtag() {
        long userId = 1;
        when(userServiceClient.getUser(userId))
                .thenReturn(new UserDto(1L, "user", "user@gmail.com"));
        var createDto = PostCreateDto.builder()
                .content("content")
                .authorId(userId)
                .hashtagIds(List.of(1L, 2L))
                .build();

        when(hashtagService.isHashtagExist(1L)).thenReturn(false);
        when(hashtagService.isHashtagExist(2L)).thenReturn(true);

        assertThrows(EntityNotFoundException.class, () -> postService.createPostDraft(createDto));
    }

    @Test
    void testPublishPostWithPostAlreadyPublish() {
        long postId = 1;
        post.setPublished(true);
        mockGetPostById(postId);

        assertThrows(BusinessException.class, () -> postService.publishPost(postId));
    }

    @Test
    void testPublishPostSuccessCase() {
        long postId = 1;
        mockGetPostById(postId);

        postService.publishPost(postId);

        verify(postRepository, atLeastOnce()).save(postArgumentCaptor.capture());
        Post capturedPost = postArgumentCaptor.getValue();
        assertTrue(capturedPost.isPublished());
    }

    @Test
    void testUpdatePostWithPostDeleted() {
        long postId = 1;
        post.setDeleted(true);
        mockGetPostById(postId);

        assertThrows(
                BusinessException.class,
                () -> postService.updatePost(postId, new PostUpdateDto())
        );
    }

    @Test
    void testUpdatePostSuccessCase() {
        long postId = 1;
        var newContent = "new content";
        List<Long> hashtagIds = null;
        mockGetPostById(postId);

        postService.updatePost(postId, new PostUpdateDto(newContent, hashtagIds, null));
        verify(postRepository, atLeastOnce()).save(postArgumentCaptor.capture());
        Post capturedPost = postArgumentCaptor.getValue();
        assertEquals(newContent, capturedPost.getContent());

    }

    @Test
    void testUpdatePostWithExistingHashtag() {
        long postId = 1;
        var newContent = "new content";
        List<Long> hashtagIds = List.of(1L);
        mockGetPostById(postId);

        when(hashtagService.isHashtagExist(1L)).thenReturn(false);
        assertThrows(EntityNotFoundException.class,
                () -> postService.updatePost(postId, new PostUpdateDto(newContent, hashtagIds, null)));

    }

    @Test
    void testSoftDeletePostWithPostDeleted() {
        long postId = 1;
        post.setDeleted(true);
        mockGetPostById(postId);

        assertThrows(
                BusinessException.class,
                () -> postService.softDeletePost(postId)
        );
    }

    @Test
    void testSoftDeletePostSuccessCase() {
        long postId = 1;
        mockGetPostById(postId);

        postService.softDeletePost(postId);

        verify(postRepository, atLeastOnce()).save(postArgumentCaptor.capture());
        Post capturedPost = postArgumentCaptor.getValue();
        assertTrue(capturedPost.isDeleted());
    }

    @Test
    void testGetAllDraftsByAuthor() {
        long authorId = 1;
        postService.getAllDrafts(authorId, PostOwnerType.AUTHOR);
        verify(postRepository, atLeastOnce()).findAllDraftsByAuthorId(authorId);
    }

    @Test
    void testGetAllDraftsByProject() {
        long projectId = 1;
        postService.getAllDrafts(projectId, PostOwnerType.PROJECT);
        verify(postRepository, atLeastOnce()).findAllDraftsByProjectId(projectId);
    }

    @Test
    void testGetAllPublishedPostsByAuthor() {
        long authorId = 1;
        postService.getAllPublished(authorId, PostOwnerType.AUTHOR);
        verify(postRepository, atLeastOnce()).findAllPublishedByAuthorId(authorId);
    }

    @Test
    void testGetAllPublishedPostsByProject() {
        long projectId = 1;
        postService.getAllPublished(projectId, PostOwnerType.PROJECT);
        verify(postRepository, atLeastOnce()).findAllPublishedByProjectId(projectId);
    }

    @Test
    void uploadImagesUploadAndSaveImages() {
        post.setResources(new ArrayList<>());
        long postId = post.getId();

        MultipartFile file = mock(MultipartFile.class);
        when(file.getSize()).thenReturn(1024L);
        when(file.getContentType()).thenReturn("image/png");

        Resource resource = new Resource();
        ReflectionTestUtils.setField(postService, "maxFiles", 10);
        when(s3Service.uploadResource(file, postId + "_post_attachments")).thenReturn(resource);
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postRepository.save(postArgumentCaptor.capture())).thenReturn(post);

        PostReadDto result = postService.uploadImages(postId, List.of(file));

        assertNotNull(result);
        verify(s3Service).uploadResource(file, postId + "_post_attachments");
        verify(resourceRepository).saveAll(anyList());
        verify(postRepository, times(1)).save(post);
    }

    @Test
    void deleteImagesRemoveImagesAndUpdatePost() {
        String fileKey = "image.png";
        Resource resource = new Resource();
        resource.setKey(fileKey);
        long postId = post.getId();
        post.setResources(new ArrayList<>(List.of(resource)));

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(resourceRepository.findByKey(fileKey)).thenReturn(resource);
        when(postRepository.save(postArgumentCaptor.capture())).thenReturn(post);

        PostReadDto result = postService.deleteImages(postId, List.of(fileKey));

        assertNotNull(result);
        verify(resourceRepository).delete(resource);
        verify(s3Service).deleteFile(fileKey);
        verify(postRepository).save(post);
    }

    @Test
    void downloadImageDownloadImage() {
        String fileKey = "image.png";
        Resource resource = new Resource();
        resource.setKey(fileKey);

        InputStream inputStream = new ByteArrayInputStream("image data".getBytes());
        when(resourceRepository.findByKey(fileKey)).thenReturn(resource);
        when(s3Service.downloadFile(fileKey)).thenReturn(inputStream);

        byte[] result = postService.downloadImage(fileKey);

        assertNotNull(result);
        assertEquals("image data", new String(result));
        verify(s3Service).downloadFile(fileKey);
    }

    @RepeatedTest(5)
    void testModeratePostsSuccessCase() {
        int pageSize = 2;
        postProperties.setPageSize(pageSize);
        postProperties.setBatchSize(1);
        var firstPagePosts = List.of(
                createPostWithContent("Content"),
                createPostWithContent("Content")
        );
        var secondPagePosts = List.of(
                createPostWithContent("Content"),
                createPostWithContent("Bad Content")

        );
        Pageable firstPageable = PageRequest.of(0, pageSize);
        Pageable secondPageable = PageRequest.of(1, pageSize);
        Page<Post> firstPage = new PageImpl<>(firstPagePosts, firstPageable, 4);
        Page<Post> secondPage = new PageImpl<>(secondPagePosts, secondPageable, 4);

        when(postRepository.findAllNotVerified(firstPageable)).thenReturn(firstPage);
        when(postRepository.findAllNotVerified(secondPageable)).thenReturn(secondPage);
        when(moderationDictionary.isAllowed("Content")).thenReturn(true);
        when(moderationDictionary.isAllowed("Bad Content")).thenReturn(false);
        ArgumentCaptor<List<Post>> argumentCaptor = ArgumentCaptor.forClass(List.class);

        postService.moderatePosts();

        verify(postRepository, times(2))
                .saveAll(argumentCaptor.capture());
        List<Post> capturedPosts1 = argumentCaptor.getAllValues().get(0);
        List<Post> capturedPosts2 = argumentCaptor.getAllValues().get(1);
        assertEquals(2, capturedPosts1.size());
        assertEquals(1, capturedPosts2.size());
        assertTrue(isVerified(capturedPosts2.get(0)));
        assertTrue(capturedPosts1.stream().allMatch(this::isVerified));
    }

    private boolean isVerified(Post post) {
        return post.isVerified()
                && post.getVerifiedDate() != null
                && post.getContent().equals("Content");
    }

    private Post createPostWithContent(String content) {
        return Post.builder().content(content).build();
    }

    private void mockGetPostById(long id) {
        when(postRepository.findById(id))
                .thenReturn(Optional.of(post));
    }
}