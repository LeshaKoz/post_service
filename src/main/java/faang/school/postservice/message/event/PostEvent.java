package faang.school.postservice.message.event;

import lombok.Builder;

import java.util.List;

@Builder
public record PostEvent(
        Long postId,
        List<Long> followerIds
) {
}
