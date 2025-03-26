package faang.school.postservice.service;

import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {
    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostService postService;

    private final Post post = new Post();
    private final Long postId = 1L;

    @Test
    @DisplayName("getPost: позитивный сценарий")
    public void givenExistingPostIdWhenGetPostEntityThenReturnPostEntity() {
        Mockito.when(postRepository.findById(postId))
                .thenReturn(Optional.of(post));

        Post returnedPost = postService.getPostEntity(postId);

        Assertions.assertNotNull(returnedPost);
    }

    @Test
    @DisplayName("getPost: пост не найден")
    public void givenNonExistingPostIdWhenGetPostEntityThenThrowEntityNotFoundException() {
        Mockito.when(postRepository.findById(postId))
                .thenReturn(Optional.empty());

        Exception exception = Assertions.assertThrows(EntityNotFoundException.class, () ->
                postService.getPostEntity(postId));

        Assertions.assertEquals("Post not found with id: 1", exception.getMessage());
    }
}
