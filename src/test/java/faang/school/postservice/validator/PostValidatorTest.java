package faang.school.postservice.validator;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostValidatorTest {

    private static final Long POST_ID = 1L;

    @Mock
    private PostRepository postRepository;
    @InjectMocks
    private PostValidator postValidator;

    @Test
    public void testGetByIdPostNotFound() {
        String errorMessage = "Post not found";
        when(postRepository.findById(POST_ID))
                .thenThrow(new EntityNotFoundException(errorMessage));

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> postValidator.getPostById(POST_ID));
        verify(postRepository, times(1)).findById(POST_ID);
        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    public void testGetPostById() {
        Post post = Post.builder()
                .id(POST_ID)
                .content("content")
                .build();

        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(post));
        Post result = postValidator.getPostById(POST_ID);

        verify(postRepository, times(1)).findById(POST_ID);
        assertNotNull(result);
        assertEquals(post.getContent(), result.getContent());
        assertEquals(post.getId(), result.getId());
    }
}
