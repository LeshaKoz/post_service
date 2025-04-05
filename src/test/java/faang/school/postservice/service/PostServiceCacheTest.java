package faang.school.postservice.service;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.ThreadPoolExecutor;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class PostServiceCacheTest {

    @Mock
    private ThreadPoolTaskExecutor publishingThreadPool;

    @Mock
    private AsyncModerationService asyncModerationService;

    @Mock
    private SpellCheckerService spellCheckerService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private InternalServices internalServices;

    @Mock
    private PostCacheService postCacheService;

    @InjectMocks
    private PostService postService;

    @BeforeEach
    void setUp() {
        ThreadPoolExecutor threadPoolExecutor = mock(ThreadPoolExecutor.class);
        when(publishingThreadPool.getThreadPoolExecutor()).thenReturn(threadPoolExecutor);
    }

    @Test
    void shouldCachePostAfterPublishing() {
        Long postId = 1L;
        Post post = new Post();
        post.setId(postId);
        post.setPublished(false);

        Post publishedPost = new Post();
        publishedPost.setId(postId);
        publishedPost.setPublished(true);
        publishedPost.setPublishedAt(LocalDateTime.now());

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postRepository.save(any(Post.class))).thenReturn(publishedPost);

        Post result = postService.publish(postId);

        verify(postCacheService).cachePost(result);
        assertTrue(result.isPublished());
    }

    @Test
    void shouldRemovePostFromCacheWhenDeleted() {
        Long postId = 1L;
        Post post = new Post();
        post.setId(postId);
        post.setDeleted(false);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        postService.delete(postId);

        verify(postRepository).save(argThat(Post::isDeleted));
        verify(postCacheService).removePostFromCache(postId);
    }

    @Test
    void shouldReturnFromCacheWhenAvailable() {
        Long postId = 1L;
        Post cachedPost = new Post();
        cachedPost.setId(postId);

        when(postRepository.findById(postId)).thenReturn(Optional.of(new Post()));
        when(postCacheService.getCachedPost(postId)).thenReturn(Optional.of(cachedPost));

        Post result = postService.get(postId);

        verify(postCacheService).getCachedPost(postId);
        verifyNoInteractions(postRepository);
        assertSame(cachedPost, result);
    }

    @Test
    void shouldGetFromDatabaseAndCacheWhenNotInCache() {
        Long postId = 1L;
        Post dbPost = new Post();
        dbPost.setId(postId);

        when(postCacheService.getCachedPost(postId)).thenReturn(Optional.empty());
        when(postRepository.findById(postId)).thenReturn(Optional.of(dbPost));

        Post result = postService.get(postId);

        verify(postCacheService).getCachedPost(postId);
        verify(postRepository).findById(postId);
        assertSame(dbPost, result);
    }
}
