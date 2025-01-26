package faang.school.postservice.service.comment;

import faang.school.postservice.dto.comment.CommentRequestDto;
import faang.school.postservice.dto.comment.CommentResponseDto;

import java.util.List;

public interface CommentService {
    CommentResponseDto createComment(CommentRequestDto commentDto);

    CommentResponseDto updateComment(long commentId, CommentRequestDto commentDto);

    List<CommentResponseDto> getComments(long postId);

    void deleteComment(long commentId);
}
