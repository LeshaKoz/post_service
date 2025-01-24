package faang.school.postservice;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CreateCommentRequest;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.CommentService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @InjectMocks
    private CommentService commentService;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Spy
    private CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);

    @Captor
    private ArgumentCaptor<Comment> commentCaptor;


    @Test
    public void createComment_commend_not_found() {
        CreateCommentRequest request = new CreateCommentRequest();
        request.setAuthorId(1L);
        when(userServiceClient.getUser(request.getAuthorId())).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> commentService.createComment(request));
    }

    @Test
    public void createComment_post_not_found() {
        CreateCommentRequest request = new CreateCommentRequest();
        request.setAuthorId(1L);
        request.setPostId(1L);
        when(userServiceClient.getUser(request.getAuthorId()))
                .thenReturn(new UserDto(1L, "Any", "Any"));
        when(postRepository.findById(request.getPostId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> commentService.createComment(request));
    }

    @Test
    public void createComment() {
        CreateCommentRequest request = new CreateCommentRequest();
        request.setAuthorId(1L);
        request.setPostId(1L);
        request.setContent("Text");
        Post post = new Post();
        Comment comment = new Comment();
        comment.setAuthorId(request.getAuthorId());
        comment.setPost(post);
        comment.setContent("Title");
        when(userServiceClient.getUser(request.getAuthorId()))
                .thenReturn(new UserDto(1L, "Any", "Any"));
        when(postRepository.findById(request.getPostId())).thenReturn(Optional.of(post));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        commentService.createComment(request);

        verify(commentRepository, times(1)).save(commentCaptor.capture());

    }
}
