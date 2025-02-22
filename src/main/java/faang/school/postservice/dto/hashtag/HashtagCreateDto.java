package faang.school.postservice.dto.hashtag;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record HashtagCreateDto(
        @NotBlank(message = "Название хештега не должно быть пустым")
        @Size(max = 64)
        String name
) {
}