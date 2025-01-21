package faang.school.postservice.dto.post;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PostRequestDto(
        Long id,
        String content,
        Long authorId,
        Long projectId,
        boolean isPublished,
        LocalDateTime publishedAt) {
}
