package faang.school.postservice.dto.comment;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PostCommentEvent(
        Long postId,
        Long commentId,
        String content,
        Long authorId,
        LocalDateTime createdAt
) {
}
