package faang.school.postservice.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ResourceDto(
        String key,
        long size,
        LocalDateTime createdAt,
        String name,
        String type,
        Long postId
) {
}

