package faang.school.postservice.dto.album;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AlbumReadDto {
    private Long id;
    private String title;
    private String description;
    private Long authorId;
}
