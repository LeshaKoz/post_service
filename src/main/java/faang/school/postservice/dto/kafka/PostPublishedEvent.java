package faang.school.postservice.dto.kafka;

import java.util.List;

public record PostPublishedEvent(
        List<Long> followersIds
) {
}
