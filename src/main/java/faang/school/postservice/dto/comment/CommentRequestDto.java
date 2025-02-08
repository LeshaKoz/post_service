package faang.school.postservice.dto.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CommentRequestDto(
        @Schema(description = "ID of the author", example = "1")
        @NotNull
        Long authorId,
        @Schema(description = "ID of the post", example = "1")
        @NotNull
        Long postId,
        @Schema(description = "Content of the comment", example = "This is a great post!")
        @NotNull
        @Size(min = 1, max = 4096, message = "Content must not exceed 4096 characters")
        String content
) { }
