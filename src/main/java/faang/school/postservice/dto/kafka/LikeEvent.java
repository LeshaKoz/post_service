package faang.school.postservice.dto.kafka;

import java.time.LocalDateTime;

public record LikeEvent(

        Long id,

        Long authorId,

        Long postId,

        LocalDateTime createdAt
) {
}
