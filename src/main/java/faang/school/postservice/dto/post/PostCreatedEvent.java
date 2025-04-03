package faang.school.postservice.dto.post;

import java.util.List;

public record PostCreatedEvent(
        Long postId,
        Long authorId,
        List<Long> subscriberIds
) {
}
