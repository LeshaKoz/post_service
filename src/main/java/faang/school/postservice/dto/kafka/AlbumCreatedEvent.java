package faang.school.postservice.dto.kafka;

public record AlbumCreatedEvent(long userId, long albumId, String title) {
}