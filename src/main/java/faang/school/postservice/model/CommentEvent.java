package faang.school.postservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CommentEvent {
    private Long commentId;
    private Long postId;
    private Long authorId;
    private String content;
    private LocalDateTime createdAt;
}
