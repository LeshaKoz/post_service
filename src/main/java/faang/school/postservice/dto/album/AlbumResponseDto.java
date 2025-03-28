package faang.school.postservice.dto.album;

public record AlbumResponseDto(
        long id,
        String title,
        String description,
        long authorId
) {
}
