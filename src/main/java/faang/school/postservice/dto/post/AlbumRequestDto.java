package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AlbumRequestDto(

        @NotNull
        @NotBlank
        String title,

        String description
) {
}
