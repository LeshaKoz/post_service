package faang.school.postservice.dto.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CommentUpdateDto(
        @NotNull
        @Schema(
                description = "Идентификатор комментария",
                example = "1"
        )
        Long id,
        @NotBlank
        @Size(max = 4096)
        @Schema(
                description = "Текст комментария",
                example = "Круто!"
        )
        String content,
        @NotNull
        @Schema(
                description = "Идентификатор автора комментария",
                example = "1"
        )
        Long editorId
) {
}
