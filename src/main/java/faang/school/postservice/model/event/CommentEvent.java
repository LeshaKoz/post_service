package faang.school.postservice.model.event;

import lombok.Data;

@Data
public class CommentEvent {
    private Long authorCommentId;
    private Long authorPostId;
    private Long postId;
    private Long content;
    private Long commentId;
}
