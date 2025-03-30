package faang.school.postservice.event.like;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record LikeEvent(long likeId, long postId, long authorId, long userId, LocalDateTime timeStamp) {
}
