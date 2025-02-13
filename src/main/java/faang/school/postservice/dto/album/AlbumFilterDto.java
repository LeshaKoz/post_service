package faang.school.postservice.dto.album;

import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record AlbumFilterDto(
        @Size(max = 256) String titlePattern,
        LocalDateTime fromDate,
        LocalDateTime toDate,
        Long authorId
) {
}