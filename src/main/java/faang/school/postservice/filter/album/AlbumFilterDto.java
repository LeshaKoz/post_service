package faang.school.postservice.filter.album;

import java.time.LocalDateTime;

public record AlbumFilterDto(String title, LocalDateTime createdAt) {

}
