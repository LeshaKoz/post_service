package faang.school.postservice.service.post;

import faang.school.postservice.dto.post.CommentDto;

import java.util.List;

public interface CommentService {

    CommentDto createComment(CommentDto dto);

    CommentDto updateComment(CommentDto dto);

    List<CommentDto> findAllCommentsByPostId(long postId);

    void deleteCommentById(long id);
}
