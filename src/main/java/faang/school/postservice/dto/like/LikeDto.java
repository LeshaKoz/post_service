package faang.school.postservice.dto.like;

import jakarta.validation.constraints.NotNull;

public record LikeDto(

        @NotNull(message = "Invalid user id value")
        Long userId
) {
}
