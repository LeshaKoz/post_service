package faang.school.postservice.dto.album;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateAlbumRequestDto(
        @NotBlank @Size(max = 256) String title,
        @NotBlank @Size(max = 4096) String description
) {
}
