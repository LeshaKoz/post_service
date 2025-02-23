package faang.school.postservice.event.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class CommentEvent {
    private Long commentId;
    private String comment;
    private Long userId;
    private Long postId;
    private CommentEventType eventType;
    private LocalDateTime createdAt;
}
