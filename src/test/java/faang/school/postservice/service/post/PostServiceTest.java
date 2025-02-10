package faang.school.postservice.service.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.config.props.PostProperties;
import faang.school.postservice.dto.post.PostCreateDto;
import faang.school.postservice.dto.post.PostOwnerType;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.BusinessException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.mapper.PostMapperImpl;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
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
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    @Mock
    private ModerationDictionary moderationDictionary;
    @Spy
    private PostMapper postMapper = new PostMapperImpl();
    @Spy
    private PostProperties postProperties;

    private PostService postService;

    private Post post;

    @Captor
    ArgumentCaptor<Post> postArgumentCaptor;

    @BeforeEach
    void setUp() {
        post = Post.builder()
                .content("content")
                .id(1L)
                .build();
        postService = new PostService(
                userServiceClient,
                projectServiceClient,
                postRepository,
                postMapper,
                userContext,
                moderationDictionary,
                executorService,
                postProperties
        );
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

        postService.updatePost(postId, new PostUpdateDto(newContent, hashtagIds));
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
                () -> postService.updatePost(postId, new PostUpdateDto(newContent, hashtagIds)));

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