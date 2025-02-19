package faang.school.postservice.dto.messaging;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record LikeEvent(long postId, long authorId, long userId, LocalDateTime timeStamp) {
}
