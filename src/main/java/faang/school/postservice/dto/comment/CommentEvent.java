package faang.school.postservice.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
public record CommentEvent(
        Long postAuthorId,
        Long commentAuthorId,
        Long postId,
        Long commentId,
        LocalDateTime commentedAt
) {
}
