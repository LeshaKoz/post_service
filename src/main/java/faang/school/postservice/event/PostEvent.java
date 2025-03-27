package faang.school.postservice.event;

import lombok.Builder;

@Builder
public record PostEvent(
        long authorId,
        long postId
) {
}