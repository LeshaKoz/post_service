package faang.school.postservice.service.post;

import faang.school.postservice.exception.PostNotFoundException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.post.implementations.PostServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;


class PostServiceImplTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostServiceImpl postService;

    private Post post;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        post = new Post();
        post.setId(1L);
        post.setContent("Test post");
        post.setAuthorId(2L);
        post.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void getPostById_success() {
        Long postId = 1L;

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        Post result = postService.getPostById(postId);

        assertNotNull(result);
        assertEquals(post, result);
        verify(postRepository).findById(postId);
        verifyNoMoreInteractions(postRepository);
    }

    @Test
    void getPostById_postNotFound_throwsException() {
        Long postId = 1L;

        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        PostNotFoundException exception = assertThrows(PostNotFoundException.class,
                () -> postService.getPostById(postId));
        assertEquals("Post with id " + postId + " not found", exception.getMessage());
        verify(postRepository).findById(postId);
        verifyNoMoreInteractions(postRepository);
    }
}
