package faang.school.postservice.service.comment;

import faang.school.postservice.dto.comment.CommentFiltersDto;
import faang.school.postservice.dto.comment.CommentRequestDto;
import faang.school.postservice.dto.comment.CommentResponseDto;
import faang.school.postservice.dto.comment.CommentUpdateDto;

import java.util.List;

public interface CommentService {
    CommentResponseDto createComment(CommentRequestDto commentDto);

    CommentResponseDto updateComment(long commentId, long authorId, CommentUpdateDto commentUpdateDto);

    List<CommentResponseDto> getComments(CommentFiltersDto commentFiltersDto);

    void deleteComment(long commentId);
}
