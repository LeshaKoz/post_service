package faang.school.postservice.dto.post;

import java.util.List;

public record AlbumResponseDto(
        long id,
        String title,
        String description,
        long authorId,
        List<Long> postsIds) {
}
