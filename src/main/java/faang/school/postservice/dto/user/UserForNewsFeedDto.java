package faang.school.postservice.dto.user;

import lombok.Builder;

import java.util.List;

@Builder
public record UserForNewsFeedDto(
        Long id,
        String username,
        List<Long> followerIds,
        List<Long> followeeIds
) {
}
