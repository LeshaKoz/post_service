package faang.school.postservice.service;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostServiceCacheIntegrationTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private InternalServices internalServices;

    @Mock
    private PostCacheService postCacheService;

    @InjectMocks
    private PostService postService;

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
        verify(postCacheService).removeFromCache(postId);
    }

    @Test
    void shouldReturnFromCacheWhenAvailable() {
        Long postId = 1L;
        Post cachedPost = new Post();
        cachedPost.setId(postId);

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
        verify(postCacheService).cachePost(dbPost);
        assertSame(dbPost, result);
    }
}
