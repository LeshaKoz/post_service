package faang.school.postservice.message.event;

import lombok.Builder;

@Builder
public record LikeEvent(
        Long postId,
        Long authorId
) {
}
