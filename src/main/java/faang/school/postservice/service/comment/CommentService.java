package faang.school.postservice.service.comment;

import faang.school.postservice.dto.comment.CommentRequestDto;
import faang.school.postservice.dto.comment.CommentResponseDto;
import faang.school.postservice.dto.comment.CommentUpdateDto;
import reactor.core.publisher.Mono;

import java.util.List;

public interface CommentService {
    public Mono<Void> moderateComments();

    public void banUsersForComments();

    public void createComment(CommentRequestDto commentRequestDto);

    public void updateComment(Long id, CommentUpdateDto commentUpdateDto);

    public List<CommentResponseDto> getCommentsByPostId(Long postId);

    public void deleteComment(Long id);
}
