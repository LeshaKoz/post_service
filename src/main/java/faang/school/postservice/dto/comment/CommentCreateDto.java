package faang.school.postservice.dto.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
@Schema
public record CommentCreateDto(
        @NotBlank
        @Size(max = 4096)
        @Schema(
                description = "Текст комментария",
                example = "Крутое видео!"
        )
        String content,
        @NotNull
        @Schema(
                description = "Id автора комментария",
                example = "1"
        )
        Long authorId,
        @NotNull
        @Schema
                (
                        description = "Id поста, к которому относится комментарий",
                        example = "1"
                )
        Long postId
) {
}
