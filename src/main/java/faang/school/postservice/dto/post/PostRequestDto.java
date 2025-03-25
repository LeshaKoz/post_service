package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotBlank;

public record PostRequestDto(
        @NotBlank(message = "Content can't be null or empty")
        String content
) {
}
