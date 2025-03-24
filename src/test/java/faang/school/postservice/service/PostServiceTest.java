package faang.school.postservice.service;

import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostServiceImpl postService;

    @Test
    public void testFindPostByIdWithThrow() {
        long postId = 1L;
        when(postRepository.findById(postId)).thenReturn(Optional.empty());
        Exception exception = assertThrows(DataValidationException.class,
                () -> postService.findPostById(postId));
        assertEquals("Post with id 1 not found", exception.getMessage());
        verify(postRepository,times(1)).findById(postId);
    }

    @Test
    public void testFindPostById() {
        long postId = 1L;
        Post post = Post.builder().id(postId).build();
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        Post postResult = postService.findPostById(postId);
        verify(postRepository, times(1)).findById(postId);
        assertEquals(postId, postResult.getId());
    }
}
