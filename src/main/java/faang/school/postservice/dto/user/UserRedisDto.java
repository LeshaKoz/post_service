package faang.school.postservice.dto.user;

import lombok.Builder;

@Builder
public record UserRedisDto(
        Long id,
        String username,
        String email,
        String content
) {
}
