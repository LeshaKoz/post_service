package faang.school.postservice.dto.album;

import faang.school.postservice.model.Visibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateAlbumRequestDto(
        @NotBlank @Size(max = 256) String title,
        @NotBlank @Size(max = 4096) String description,
        @NotNull Visibility visibility
) {
}
