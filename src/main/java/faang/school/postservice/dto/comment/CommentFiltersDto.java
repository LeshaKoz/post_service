package faang.school.postservice.dto.comment;

import lombok.Builder;

@Builder
public record CommentFiltersDto(
        Long postId
) {
}
