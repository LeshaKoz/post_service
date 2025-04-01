package faang.school.postservice.dto.kafka;

import java.time.LocalDateTime;
import java.util.List;

public record PostEvent(

        Long postId,

        Long authorId,

        LocalDateTime createdAt,

        List<Long> followersIds,

        List<String> comments
) {
}
