package faang.school.postservice.service.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.ResponseCommentDto;
import faang.school.postservice.model.entity.Comment;
import faang.school.postservice.model.entity.Post;
import java.util.List;

public class CommentTestData {
    public static Comment getComment(Long id, String text, Long userId, Long postId) {
        return Comment.builder()
                .id(id)
                .content(text)
                .authorId(userId)
                .post(Post.builder().id(postId).build())
                .build();
    }

    public static CommentDto getCommentDto(Long id, String text, Long userId, Long postId) {
        return CommentDto.builder()
                .id(id)
                .content(text)
                .authorId(userId)
                .postId(postId)
                .build();
    }

    public static ResponseCommentDto getResponseCommentDto(Long id, String text, Long userId, Long postId, List<Long> likes) {
        return ResponseCommentDto.builder()
                .id(id)
                .content(text)
                .authorId(userId)
                .postId(postId)
                .likeIds(likes)
                .build();
    }
}
