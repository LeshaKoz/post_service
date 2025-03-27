package faang.school.postservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommentEvent {
    private Long commentId;
    private Long postId;
    private Long authorId;
}
