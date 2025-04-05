package faang.school.postservice.event;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record PostEvent(
        long authorId,
        long postId,
        List<Long> subscriberIds,
        LocalDateTime createdAt
) {
}