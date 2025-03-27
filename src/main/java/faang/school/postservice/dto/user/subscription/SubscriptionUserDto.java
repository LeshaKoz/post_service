package faang.school.postservice.dto.user.subscription;

import lombok.Builder;

@Builder
public record SubscriptionUserDto(

        Long id,

        String username,

        String email
) {
}
