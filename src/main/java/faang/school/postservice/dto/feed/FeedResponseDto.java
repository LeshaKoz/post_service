package faang.school.postservice.dto.feed;

import java.time.LocalDateTime;

public record FeedResponseDto
    (String authorName,
     String content,
     LocalDateTime publishedAt
    ) {
}
