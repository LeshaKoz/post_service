package faang.school.postservice.dto.album;

import java.time.LocalDateTime;
import java.util.List;

public record AlbumDto(
        Long id,
        String title,
        String description,
        Long authorId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<Long> postIds
) {
}
