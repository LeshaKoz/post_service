package faang.school.postservice.dto.post;

import lombok.Builder;

@Builder
public record PostCreateRequestDto(
        String content,
        Long authorId,
        Long projectId) {
}
