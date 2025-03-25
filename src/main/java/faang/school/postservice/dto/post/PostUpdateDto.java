package faang.school.postservice.dto.post;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Класс `PostUpdateDto` используется для передачи данных, необходимых для обновления существующего поста.
 * Содержит минимальный набор полей, которые могут быть изменены при обновлении поста.
 *
 * <p>Основные поля:
 * <ul>
 *     <li>Содержимое поста ({@link #content})</li>
 *     <li>Флаг публикации ({@link #published})</li>
 *     <li>Флаг удаления поста ({@link #deleted})</li>
 *     <li>Список идентификаторов ресурсов ({@link #resourceIds})</li>
 *     <li>Дата запланированной публикации ({@link #scheduledAt})</li>
 * </ul>
 * </p>
 *
 * @author marsel_mkh
 */
@Data
public class PostUpdateDto {
    @Schema(description = "Содержимое поста",
            example = "content", accessMode = Schema.AccessMode.WRITE_ONLY)
    @NotBlank(message = "Content is not blank")
    private String content;

    @Schema(description = "Флаг публикации",
            example = "true")
    private boolean published;

    @Schema(description = "Флаг удаления поста",
            example = "true")
    private boolean deleted;

    @Schema(description = "Список идентификаторов ресурсов",
            example = "[1,2,3]")
    private List<Long> resourceIds;

    @Schema(description = "Дата запланированной публикации",
            example = "2026-01-01T15:55:00")
    private LocalDateTime scheduledAt;
}
