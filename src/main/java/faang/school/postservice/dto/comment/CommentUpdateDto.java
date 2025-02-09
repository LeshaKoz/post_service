package faang.school.postservice.dto.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CommentUpdateDto(
        @Schema(description = "New content of the comment", example = "This is a new comment")
        @NotNull
        @Size(min = 1, max = 4096, message = "Content must not exceed 4096 characters")
        String content
) {
}
