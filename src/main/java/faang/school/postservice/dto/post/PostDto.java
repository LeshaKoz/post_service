package faang.school.postservice.dto.post;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class PostDto {
    @Schema(description = "Unique identifier of the post", example = "1")
    private final Long id;
    @NotEmpty(message = "Post cannot be empty")
    @Schema(description = "Content of the post", example = "This is a sample post content")
    private final String content;
    @Schema(description = "Identifier of the author", example = "1")
    private Long authorId;
    @Schema(description = "Identifier of the project", example = "1")
    private Long projectId;
    private LocalDateTime scheduledAt;
}
