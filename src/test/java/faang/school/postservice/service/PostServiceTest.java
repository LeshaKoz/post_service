package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.Post.PostCacheDto;
import faang.school.postservice.dto.Post.CreatePostDraftDto;
import faang.school.postservice.dto.Post.PostResponseDto;
import faang.school.postservice.dto.Post.UpdatePostDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.mapper.UserMapper;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.RedisPostAuthorRepository;
import faang.school.postservice.repository.RedisPostRepository;
import faang.school.postservice.validator.PostValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {
    private PostRepository postRepository;
    private RedisPostRepository cacheRepository;
    private RedisPostAuthorRepository postAuthorCacheRepository;

    private PostMapper postMapper;
    private UserMapper userMapper;

    private UserServiceClient userServiceClient;
    private PostValidator postValidator;
    private PostService postService;
    private ResourseService resourseService;
    private KafkaTemplate<String, Long> kafkaTemplate;
    @Captor
    private ArgumentCaptor<List<MultipartFile>> captor;

    @BeforeEach
    public void setUp() {
        postRepository = mock(PostRepository.class);
        cacheRepository = mock(RedisPostRepository.class);
        postAuthorCacheRepository = mock(RedisPostAuthorRepository.class);
        kafkaTemplate = mock(KafkaTemplate.class);
        postMapper = Mappers.getMapper(PostMapper.class);
        userMapper = Mappers.getMapper(UserMapper.class);
        postValidator = mock(PostValidator.class);
        resourseService = mock(ResourseService.class);
        userServiceClient = mock(UserServiceClient.class);
        postService = new PostService(postRepository, cacheRepository, postAuthorCacheRepository, kafkaTemplate,
                postMapper, userMapper, postValidator, resourseService, userServiceClient);
    }

    @Test
    public void createDraft_ShouldSaveWhenValidationPass() {
        CreatePostDraftDto postDraftDto = new CreatePostDraftDto();
        Post post = postMapper.fromCreateDto(postDraftDto);
        when(postRepository.save(post)).thenReturn(post);

        Assertions.assertEquals(postMapper.toResponseDto(post), postService.createDraft(postDraftDto));
        verify(postRepository, times(1)).save(postMapper.fromCreateDto(postDraftDto));
    }


    @Test
    public void createDraft_ShouldThrowWhenValidationFails() {
        CreatePostDraftDto postDraftDto = new CreatePostDraftDto();
        Post post = postMapper.fromCreateDto(postDraftDto);
        doThrow(new DataValidationException("Validation failed")).when(postValidator).validatePostAuthorExist(post);

        assertThrows(DataValidationException.class, () -> postService.createDraft(postDraftDto));
        verify(postRepository, never()).save(post);
    }

    @Test
    public void publishPost_ShouldThrowWhenPostNotFound() {
        when(postRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> postService.publishPost(1L));
    }

    @Test
    public void publishPost_ShouldThrowWhenAlreadyPublished() {
        long postId = 1L;
        Post post = new Post();

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        doThrow(new DataValidationException("test message")).when(postValidator).validateNotPublished(post);

        assertThrows(DataValidationException.class, () -> postService.publishPost(postId));
        verify(postRepository, never()).save(post);
    }


    @Test
    public void publishPost_ShouldSaveAndReturnWhenValid() {
        long postId = 1L;
        Post post = new Post();
        post.setPublished(false);
        post.setAuthorId(1L);

        UserDto userDto = new UserDto(1L, "Bob", "Bob@mail.com");

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postRepository.save(post)).thenReturn(post);
        when(userServiceClient.getUser(1)).thenReturn(userDto);

        PostResponseDto actualResponse = postService.publishPost(postId);

        assertTrue(actualResponse.isPublished());
        assertNotNull(actualResponse.getPublishedAt());
        verify(postRepository, times(1)).save(post);
        verify(cacheRepository, times(1)).save(any(PostCacheDto.class));
        verify(postAuthorCacheRepository, times(1))
                .save(userMapper.toPostAuthorCacheDto(userDto));
    }

    @Test
    public void updatePost_ShouldThrowWhenPostNotFound() {
        UpdatePostDto postDto = new UpdatePostDto();
        postDto.setId(1L);
        when(postRepository.findById(postDto.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> postService.updatePost(postDto));
    }

    @Test
    public void updatePost_ShouldThrowWhenValidationFails() {
        UpdatePostDto postDto = new UpdatePostDto();
        postDto.setId(1L);
        Post post = new Post();

        when(postRepository.findById(postDto.getId())).thenReturn(Optional.of(post));
        doThrow(new DataValidationException("Validation failed")).when(postValidator).validatePostDraftInfo(post);

        assertThrows(DataValidationException.class, () -> postService.updatePost(postDto));
        verify(postRepository, never()).save(post);
    }

    @Test
    public void updatePost_ShouldNotThrowWhenValid() {
        UpdatePostDto postDto = new UpdatePostDto();
        postDto.setId(1L);
        Post post = new Post();

        when(postRepository.findById(postDto.getId())).thenReturn(Optional.of(post));
        when(postRepository.save(post)).thenReturn(post);

        PostResponseDto actualResponse = postService.updatePost(postDto);

        assertEquals(postMapper.toResponseDto(post), actualResponse);
        verify(postRepository, times(1)).save(post);
    }

    @Test
    public void safeDeletePost_ShouldThrowWhenPostNotFound() {
        long postId = 1L;
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> postService.safeDeletePost(postId));
    }

    @Test
    public void safeDeletePost_ShouldThrowWhenValidationFails() {
        long postId = 1L;
        Post post = new Post();
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        doThrow(new DataValidationException("Post already deleted")).when(postValidator).validateNotDeleted(post);

        assertThrows(DataValidationException.class, () -> postService.safeDeletePost(postId));
        verify(postRepository, never()).save(post);
    }

    @Test
    public void safeDeletePost_ShouldSaveAndReturnWhenValid() {
        long postId = 1L;
        Post post = new Post();
        post.setDeleted(false);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postRepository.save(post)).thenReturn(post);

        PostResponseDto actualResponse = postService.safeDeletePost(postId);

        assertTrue(actualResponse.isDeleted());
        verify(postRepository, times(1)).save(post);
    }


    @Test
    public void getPost_ShouldThrowWhenPostNotFound() {
        long postId = 1L;
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> postService.getPost(postId));
    }

    @Test
    public void getPost_ShouldReturnWhenPostExists() {
        long postId = 1L;
        Post post = new Post();
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        PostResponseDto actualResponse = postService.getPost(postId);

        assertEquals(postMapper.toResponseDto(post), actualResponse);
    }

    @Test
    public void getUserDrafts_ShouldReturnDrafts() {
        long userId = 1L;
        Post post = new Post();
        post.setPublished(false);
        post.setDeleted(false);
        List<Post> posts = List.of(post);

        when(postRepository.findByAuthorId(userId)).thenReturn(posts);

        List<PostResponseDto> result = postService.getUserDrafts(userId);

        assertEquals(1, result.size());
        assertFalse(result.get(0).isPublished());
        assertFalse(result.get(0).isDeleted());
    }

    @Test
    public void getProjectDrafts_ShouldReturnDrafts() {
        long projectId = 1L;
        Post post = new Post();
        post.setPublished(false);
        post.setDeleted(false);
        List<Post> posts = List.of(post);

        when(postRepository.findByProjectId(projectId)).thenReturn(posts);

        List<PostResponseDto> result = postService.getProjectDrafts(projectId);

        assertEquals(1, result.size());
        assertFalse(result.get(0).isPublished());
        assertFalse(result.get(0).isDeleted());
    }

    @Test
    public void getUserPosts_ShouldReturnPosts() {
        Post post = new Post();
        post.setId(100L);
        post.setPublished(true);
        post.setDeleted(false);
        post.setLikes(List.of(new Like(), new Like()));
        post.setPublishedAt(LocalDateTime.now());

        long userId = 1L;
        when(postRepository.findByAuthorIdWithLikes(userId)).thenReturn(List.of(post));

        List<PostResponseDto> result = postService.getUserPosts(userId);

        assertEquals(1, result.size());
        assertTrue(result.get(0).isPublished());
        assertFalse(result.get(0).isDeleted());
        assertEquals(2, result.get(0).getLikesCount());
        assertEquals(2, result.get(0).getLikesIds().size());
    }

    @Test
    public void getUserPosts_ShouldCorrectlySort() {
        Post olderPost = new Post();
        olderPost.setPublished(true);
        olderPost.setDeleted(false);
        olderPost.setPublishedAt(LocalDateTime.of(2023, 1, 1, 10, 0));
        Post newerPost = new Post();
        newerPost.setPublished(true);
        newerPost.setDeleted(false);
        newerPost.setPublishedAt(LocalDateTime.of(2024, 1, 1, 10, 0));

        long userId = 1L;
        when(postRepository.findByAuthorIdWithLikes(userId)).thenReturn(List.of(olderPost, newerPost));

        List<PostResponseDto> result = postService.getUserPosts(userId);

        assertEquals(2, result.size());
        assertTrue(result.get(0).getPublishedAt().isAfter(result.get(1).getPublishedAt()));
    }

    @Test
    public void getProjectPosts_ShouldReturnPosts() {
        Post post = new Post();
        post.setId(200L);
        post.setPublished(true);
        post.setDeleted(false);
        post.setLikes(List.of(new Like()));
        post.setPublishedAt(LocalDateTime.now());

        long projectId = 1L;
        when(postRepository.findByProjectIdWithLikes(projectId)).thenReturn(List.of(post));

        List<PostResponseDto> result = postService.getProjectPosts(projectId);

        assertEquals(1, result.size());
        assertTrue(result.get(0).isPublished());
        assertFalse(result.get(0).isDeleted());
        assertEquals(1, result.get(0).getLikesCount());
        assertEquals(1, result.get(0).getLikesIds().size());
    }

    @Test
    public void getProjectPosts_ShouldCorrectlySort() {
        Post olderPost = new Post();
        olderPost.setPublished(true);
        olderPost.setDeleted(false);
        olderPost.setPublishedAt(LocalDateTime.of(2022, 5, 5, 10, 0));
        Post newerPost = new Post();
        newerPost.setPublished(true);
        newerPost.setDeleted(false);
        newerPost.setPublishedAt(LocalDateTime.of(2023, 5, 5, 10, 0));

        long projectId = 1L;
        when(postRepository.findByProjectIdWithLikes(projectId)).thenReturn(List.of(olderPost, newerPost));

        List<PostResponseDto> result = postService.getProjectPosts(projectId);

        assertEquals(2, result.size());
        assertTrue(result.get(0).getPublishedAt().isAfter(result.get(1).getPublishedAt()));
    }

    @Test
    public void uploadImages_whenPostNotFound() {
        Long postId = 1L;
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> postService.uploadImages(postId, captor.capture()));
    }

    @Test
    public void uploadImages() {
        Long postId = 1L;
        Post post = new Post();
        List<MultipartFile> files = Arrays.asList(mock(MultipartFile.class), mock(MultipartFile.class));

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        postService.uploadImages(postId, files);
    }
}