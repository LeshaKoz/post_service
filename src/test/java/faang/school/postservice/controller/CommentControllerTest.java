package faang.school.postservice.controller;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.CommentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CommentControllerTest {

    @Mock
    private CommentService commentService;
    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private CommentController commentController;

    @Test
    void getComment() {
        Long commentId = 123765L;
        Comment comment = new Comment();
        CommentDto dto = new CommentDto();

        Mockito.when(commentService.getComment(commentId)).thenReturn(comment);
        Mockito.when(commentMapper.toDto(Mockito.any(Comment.class))).thenReturn(dto);

        assertEquals(dto, commentController.getComment(commentId));
        Mockito.verify(commentService, Mockito.times(1)).getComment(Mockito.any());
        Mockito.verify(commentMapper, Mockito.times(1)).toDto(Mockito.any());
    }

    @Test
    void getComments() {
        Long postId = 123765L;
        List<Comment> comments = List.of(new Comment(), new Comment());
        int countOfElements = comments.size();

        Mockito.when(commentService.getComments(postId)).thenReturn(comments);
        Mockito.when(commentMapper.toDto(Mockito.any(Comment.class))).thenReturn(new CommentDto());

        List<CommentDto> returnedDtos = commentController.getComments(postId);

        assertEquals(countOfElements, returnedDtos.size());
        Mockito.verify(commentService, Mockito.times(1)).getComments(Mockito.any());
        Mockito.verify(commentMapper, Mockito.times(countOfElements)).toDto(Mockito.any());
    }

    @Test
    void createComment() {
        Long postId = 123765L;
        CommentDto sentDto = new CommentDto();
        Comment comment = new Comment();

        Mockito.when(commentMapper.toEntity(Mockito.any(CommentDto.class))).thenReturn(comment);
        Mockito.when(commentService.createComment(postId, comment)).thenReturn(comment);
        Mockito.when(commentMapper.toDto(Mockito.any(Comment.class))).thenReturn(sentDto);

        CommentDto returnedDto = commentController.createComment(postId, sentDto);

        Mockito.verify(commentMapper, Mockito.times(1)).toDto(Mockito.any());
        Mockito.verify(commentMapper, Mockito.times(1)).toEntity(Mockito.any());
        Mockito.verify(commentService, Mockito.times(1)).createComment(Mockito.any(), Mockito.any());
    }

    @Test
    void updateComment() {
        Long postId = 123765L;
        CommentDto sentDto = new CommentDto();
        Comment comment = new Comment();

        Mockito.when(commentMapper.toEntity(Mockito.any(CommentDto.class))).thenReturn(comment);
        Mockito.when(commentService.updateComment(postId, comment)).thenReturn(comment);
        Mockito.when(commentMapper.toDto(Mockito.any(Comment.class))).thenReturn(sentDto);

        CommentDto returnedDto = commentController.updateComment(postId, sentDto);

        Mockito.verify(commentMapper, Mockito.times(1)).toDto(Mockito.any());
        Mockito.verify(commentMapper, Mockito.times(1)).toEntity(Mockito.any());
        Mockito.verify(commentService, Mockito.times(1)).updateComment(Mockito.any(), Mockito.any());
    }

    @Test
    void deleteComment() {
        Long commentId = 123765L;

        Mockito.when(commentController.deleteComment(commentId)).thenReturn(true);

        assertTrue(commentService.deleteComment(commentId));
        Mockito.verify(commentService, Mockito.times(1)).deleteComment(Mockito.any());
    }
}