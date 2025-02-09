package faang.school.postservice.dto.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record CommentFiltersDto(
        @Schema(description = "ID of the post", example = "1")
        @NotNull
        Long postId
) {
}
