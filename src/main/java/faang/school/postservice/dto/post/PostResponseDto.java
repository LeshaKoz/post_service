package faang.school.postservice.dto.post;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PostResponseDto(
        Long id,
        String content,
        Long authorId,
        Long projectId,
        long postLikesCounter,
        boolean isPublished,
        LocalDateTime createdAt,
        LocalDateTime publishedAt) {
}
