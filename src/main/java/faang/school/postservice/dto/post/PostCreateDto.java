package faang.school.postservice.dto.post;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Класс `PostCreateDto` используется для передачи данных, необходимых для создания нового поста.
 * Содержит минимальный набор полей, которые должны быть предоставлены при создании поста.
 *
 * <p>Основные поля:
 * <ul>
 *     <li>Содержимое поста ({@link #content})</li>
 *     <li>Идентификатор автора ({@link #authorId})</li>
 *     <li>Идентификатор проекта ({@link #projectId})</li>
 *     <li>Дата запланированной публикации ({@link #scheduledAt})</li>
 * </ul>
 * </p>
 *
 * @author marsel_mkh
 * @version 1.0
 */
@Data
public class PostCreateDto {
    @Schema(description = "Содержимое поста",
            example = "content", accessMode = Schema.AccessMode.WRITE_ONLY)
    @NotBlank(message = "Content is not blank")
    private String content;

    @Schema(description = "Идентификатор автора",
            example = "1", accessMode = Schema.AccessMode.WRITE_ONLY)
    private Long authorId;

    @Schema(description = "Идентификатор проекта",
            example = "1")
    private Long projectId;

    @Schema(description = "Дата запланированной публикации",
            example = "2026-01-01T15:55:00")
    private LocalDateTime scheduledAt;
}
