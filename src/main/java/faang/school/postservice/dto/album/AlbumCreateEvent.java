package faang.school.postservice.dto.album;

public record AlbumCreateEvent(Long userId, Long albumId, String albumTitle) {
}
