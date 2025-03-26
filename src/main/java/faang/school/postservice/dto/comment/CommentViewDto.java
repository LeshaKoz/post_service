package faang.school.postservice.dto.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO для отображения информации о комментарии.
 * Содержит все данные, необходимые для отображения комментария: идентификатор, текст, автора, дату создания и другие.
 *
 * @author Zhltsk-V
 * @version 1.0
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO representing a comment view")
public class CommentViewDto {

    @Schema(
            description = "Unique identifier of the comment",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;

    @Schema(
            description = "Content text of the comment",
            example = "This post was really helpful!",
            maxLength = 4096
    )
    private String content;

    @Schema(
            description = "ID of the comment author",
            example = "42",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long authorId;

    @Schema(
            description = "ID of the post this comment belongs to",
            example = "123",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long postId;

    @Schema(
            description = "Timestamp when the comment was created",
            example = "2023-05-15T10:30:00",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private LocalDateTime createdAt;

    @Schema(
            description = "Timestamp when the comment was last updated",
            example = "2023-05-16T14:45:30",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private LocalDateTime updatedAt;

    @Schema(
            description = "File key for large attached image (if any)",
            example = "comments/1/large-image.jpg",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private String largeImageFileKey;

    @Schema(
            description = "File key for small attached image (if any)",
            example = "comments/1/small-image.jpg",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private String smallImageFileKey;
}