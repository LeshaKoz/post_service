package faang.school.postservice.dto.user;

import lombok.Builder;

@Builder
public record UserForNewsFeedResponseDto(
        Long id,
        String username
) {
}
