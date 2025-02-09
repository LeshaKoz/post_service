package faang.school.postservice.util.service.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.PostWasDeletedException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.post.AsyncPostPublishPerformer;
import faang.school.postservice.service.post.PostService;
import org.apache.commons.collections4.ListUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private ProjectServiceClient projectServiceClient;

    @Mock
    private AsyncPostPublishPerformer publishPerformer;

    @InjectMocks
    private PostService postService;

    private Long userId;
    private UserDto user;
    private Long projectId;
    private ProjectDto project;
    private Post firstPost;
    private Post secondPost;

    @BeforeEach
    void setUp() {
        userId = 1L;
        projectId = 1L;
        user = UserDto.builder()
                .id(userId)
                .build();

        project = ProjectDto.builder()
                .id(projectId)
                .build();

        firstPost = Post.builder()
                .id(1L)
                .content("Content1")
                .authorId(userId)
                .projectId(projectId)
                .published(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deleted(true)
                .build();

        secondPost = Post.builder()
                .id(2L)
                .content("Content2")
                .authorId(userId)
                .projectId(projectId)
                .published(false)
                .publishedAt(null)
                .updatedAt(LocalDateTime.now())
                .deleted(false)
                .build();
    }

    @Test
    public void testCreatePostByUserId_Success() {
        when(postRepository.save(any(Post.class))).thenReturn(firstPost);
        userServiceClient.getUser(userId);
        postService.createPostByUserId(userId, firstPost);

        verify(postRepository, times(1)).save(any(Post.class));
        assertEquals(userId, firstPost.getAuthorId());
        assertNotNull(firstPost.getCreatedAt());
        assertNotNull(firstPost.getUpdatedAt());
    }

    @Test
    public void testCreatePostByProjectId_Success() {
        when(postRepository.save(any(Post.class))).thenReturn(firstPost);
        projectServiceClient.getProject(projectId);
        postService.createPostByProjectId(projectId, firstPost);

        verify(postRepository, times(1)).save(any(Post.class));
        assertEquals(projectId, firstPost.getProjectId());
        assertNotNull(firstPost.getCreatedAt());
        assertNotNull(firstPost.getUpdatedAt());
    }

    @Test
    public void testPublishPost() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(firstPost));
        when(postRepository.save(any(Post.class))).thenReturn(firstPost);

        postService.publishPost(1L);

        verify(postRepository, times(1)).save(any(Post.class));
        assertTrue(firstPost.isPublished());
        assertNotNull(firstPost.getPublishedAt());
    }

    @Test
    public void testUpdatePost() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(firstPost));
        when(postRepository.save(any(Post.class))).thenReturn(firstPost);

        postService.updatePost(1L, secondPost);

        verify(postRepository, times(1)).save(any(Post.class));
        assertEquals("Content2", secondPost.getContent());
        assertNotNull(secondPost.getUpdatedAt());
    }

    @Test
    public void testSoftDeletePost() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(firstPost));
        when(postRepository.save(any(Post.class))).thenReturn(firstPost);

        postService.softDeletePost(1L);

        verify(postRepository, times(1)).save(any(Post.class));
        assertTrue(firstPost.isDeleted());
    }

    @Test
    public void testGetPostById_PostNotDeleted() {
        when(postRepository.findById(2L)).thenReturn(Optional.of(secondPost));

        Post result = postService.getPostById(2L);

        assertSame(secondPost, result);
    }

    @Test
    public void testGetPostById_PostDeleted() {
        when(postRepository.findById(2L)).thenReturn(Optional.of(firstPost));

        assertThrows(PostWasDeletedException.class, () -> postService.getPostById(2L));
    }

    @Test
    public void testGetNotPublishedPostsByUser() {
        when(userServiceClient.getUser(userId)).thenReturn(user);
        when(postRepository.findByAuthorId(userId)).thenReturn(Arrays.asList(firstPost, secondPost));

        List<Post> result = postService.getNotPublishedPostsByUser(userId);

        assertEquals(1, result.size());
        assertSame(secondPost, result.get(0));

        verify(userServiceClient, times(1)).getUser(userId);
    }

    @Test
    public void testGetNotPublishedPostsByProject() {
        when(projectServiceClient.getProject(projectId)).thenReturn(project);
        when(postRepository.findByProjectId(projectId)).thenReturn(Arrays.asList(firstPost, secondPost));

        List<Post> result = postService.getNotPublishedPostsByProject(projectId);

        assertEquals(1, result.size());
        assertSame(secondPost, result.get(0));
    }

    @Test
    public void testGetPublishedPostsByUser() {
        when(userServiceClient.getUser(userId)).thenReturn(user);
        when(postRepository.findByAuthorIdWithLikes(userId)).thenReturn(Arrays.asList(firstPost, secondPost));

        List<Post> result = postService.getPublishedPostsByUser(userId);

        assertEquals(1, result.size());
        assertSame(firstPost, result.get(0));
    }

    @Test
    public void testGetPublishedPostsByProject() {
        when(projectServiceClient.getProject(projectId)).thenReturn(project);
        when(postRepository.findByProjectIdWithLikes(projectId)).thenReturn(Arrays.asList(firstPost, secondPost));

        List<Post> result = postService.getPublishedPostsByProject(projectId);

        assertEquals(1, result.size());
        assertSame(firstPost, result.get(0));
    }

    @Test
    public void testPublishScheduledPosts_Success() {
        postService = new PostService(postRepository, userServiceClient, projectServiceClient, publishPerformer);
        int testBatchSize = 2;
        ReflectionTestUtils.setField(postService, "batchSize", testBatchSize);
        List<Post> posts = List.of(new Post(), new Post(), new Post(), new Post(), new Post());

        when(postRepository.findReadyToPublish()).thenReturn(posts);

        postService.publishScheduledPosts();

        ArgumentCaptor<List<Post>> batchCaptor = ArgumentCaptor.forClass(List.class);
        verify(publishPerformer, atLeastOnce()).publishBatch(batchCaptor.capture());
        List<List<Post>> actualBatches = batchCaptor.getAllValues();

        List<List<Post>> expectedBatches = ListUtils.partition(posts, testBatchSize);

        assertEquals(expectedBatches.size(), actualBatches.size(), "Invalid amount invocations of publishBatch()");

        IntStream.range(0, expectedBatches.size())
                .forEach(i -> assertEquals(
                        expectedBatches.get(i),
                        actualBatches.get(i),
                        String.format("Invalid data in batch #%d", i + 1)
                ));
    }

    @Test
    public void testPublishScheduledPosts_EmptyList() {
        when(postRepository.findReadyToPublish()).thenReturn(Collections.emptyList());

        postService.publishScheduledPosts();

        verify(postRepository, never()).saveAll(anyList());
    }
}