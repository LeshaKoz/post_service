package faang.school.postservice.dto.post;

import lombok.Builder;

@Builder
public record PostFilterDto(
        Boolean isPublished,
        Long projectId,
        Long authorId) {
}
