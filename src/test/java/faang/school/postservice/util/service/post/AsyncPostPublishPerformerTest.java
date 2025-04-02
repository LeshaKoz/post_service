package faang.school.postservice.util.service.post;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostCacheRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.post.AsyncPostPublishPerformer;
import faang.school.postservice.service.post.PostCreatedAsyncService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AsyncPostPublishPerformerTest {
    private static final Logger log = LoggerFactory.getLogger(AsyncPostPublishPerformerTest.class);

    @Mock
    private PostCreatedAsyncService postCreatedAsyncService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostCacheRepository postCacheRepository;

    @InjectMocks
    private AsyncPostPublishPerformer performer;

    @Test
    void shouldPublishBatchAsync() {
        List<Post> posts = List.of(
                Post.builder()
                        .id(1L)
                        .published(false)
                        .publishedAt(null)
                        .build(),
                Post.builder()
                        .id(2L)
                        .published(false)
                        .publishedAt(null)
                        .build()
        );
        performer.publishBatch(posts);

        await().atMost(5, SECONDS).untilAsserted(() -> {

            ArgumentCaptor<List<Post>> captor = ArgumentCaptor.forClass(List.class);
            verify(postRepository, times(1)).saveAll(captor.capture());
            verify(postCacheRepository, times(1)).saveAll(captor.capture());
            List<Post> savedPosts = captor.getValue();

            assertEquals(2, savedPosts.size());
            savedPosts.forEach(post -> {
                assertTrue(post.isPublished(), "Post is not marked as published");
                verify(postCreatedAsyncService, times(1)).processPostCreated(post);
                assertNotNull(post.getPublishedAt(), "PublishedAt is null");
            });

            log.info("Async test passed: all posts are published!");
        });
    }
}