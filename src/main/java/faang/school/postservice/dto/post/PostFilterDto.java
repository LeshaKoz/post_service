package faang.school.postservice.dto.post;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PostFilterDto(
        Boolean isPublished,
        Long projectId,
        Long authorId,
        Boolean isDeleted,
        LocalDateTime shouldBePublishedBefore
        ) {
}
