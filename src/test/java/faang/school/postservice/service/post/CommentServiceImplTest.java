package faang.school.postservice.service.post;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.CommentDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.post.CommentMapperImpl;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.post.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.post.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @Spy
    private CommentMapperImpl commentMapper;

    @InjectMocks
    private CommentServiceImpl commentService;

    @Test
    public void testCreateComment() {
        CommentDto commentDto = new CommentDto(null, "Test content", 1L, 1L);
        Comment comment = Comment.builder().id(1L).authorId(1L).post(Post.builder().id(1L).build()).content("Test content").build();
        when(userServiceClient.getUser(eq(1L)))
                .thenReturn(new UserDto(1L, "username", "email", "phone"));
        when(postRepository.findById(eq(1L))).thenReturn(Optional.ofNullable(Post.builder().id(1L).build()));
        when(commentRepository.save(any())).thenReturn(comment);

        CommentDto result = commentService.createComment(commentDto);

        CommentDto createdCommentDto = new CommentDto(1L, "Test content", 1L, 1L);
        assertNotNull(result);
        assertEquals(createdCommentDto, result);
    }

}