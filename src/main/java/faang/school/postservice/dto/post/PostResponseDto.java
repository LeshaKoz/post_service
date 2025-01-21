package faang.school.postservice.dto.post;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PostResponseDto(
        Long id,
        String content,
        Long authorId,
        Long projectId,
        boolean isPublished,
        LocalDateTime publishedAt) {
}
