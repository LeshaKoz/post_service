package faang.school.postservice.service.post;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostSchedulerServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @InjectMocks
    private PostSchedulerService postSchedulerService;

    @BeforeEach
    void setUp() {
        postSchedulerService = new PostSchedulerService(postRepository, threadPoolTaskExecutor);
        ReflectionTestUtils.setField(postSchedulerService, "maxRetries", 5);
        ReflectionTestUtils.setField(postSchedulerService, "batchCount", 3);
    }

    @Test
    void testPublishScheduledPosts_NoPosts() {
        Page<Post> emptyPage = new PageImpl<>(List.of());
        when(postRepository.findReadyToPublish(any(PageRequest.class))).thenReturn(emptyPage);

        postSchedulerService.publishScheduledPosts(10);

        verify(postRepository, times(1)).findReadyToPublish(any(PageRequest.class));
        verifyNoMoreInteractions(postRepository);
    }

    @Test
    void shouldPublishScheduledPosts_whenPostsAreAvailable() {
        Post post1 = new Post();
        post1.setAuthorId(1L);
        post1.setPublished(false);

        Post post2 = new Post();
        post2.setAuthorId(2L);
        post2.setPublished(false);

        List<Post> posts = List.of(post1, post2);
        Page<Post> postPage = new PageImpl<>(posts, PageRequest.of(0, 2), 1);

        when(postRepository.findReadyToPublish(any(PageRequest.class))).thenReturn(postPage);

        List<Post> processedPosts = posts.stream()
                .peek(post -> {
                    post.setPublished(true);
                    post.setPublishedAt(LocalDateTime.now());
                })
                .toList();

        Future<List<Post>> future = CompletableFuture.completedFuture(processedPosts);
        when(threadPoolTaskExecutor.submit(any(Callable.class))).thenReturn(future);

        postSchedulerService.publishScheduledPosts(2);

        verify(threadPoolTaskExecutor, times(1)).submit(any(Callable.class));

        ArgumentCaptor<List<Post>> postsCaptor = ArgumentCaptor.forClass(List.class);
        verify(postRepository, times(1)).saveAll(postsCaptor.capture());

        List<Post> savedPosts = postsCaptor.getValue();
        assertThat(savedPosts).hasSize(2);
        assertThat(savedPosts).allMatch(post -> post.isPublished() && post.getPublishedAt() != null);
    }
}