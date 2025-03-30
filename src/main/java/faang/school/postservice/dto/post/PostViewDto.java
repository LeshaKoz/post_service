package faang.school.postservice.dto.post;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Класс `PostViewDto` используется для передачи данных, необходимых для отображения информации о посте.
 * Содержит основные поля поста, а также идентификаторы связанных сущностей (лайки, комментарии, ресурсы и т.д.).
 *
 * <p>Основные поля:
 * <ul>
 *     <li>Уникальный идентификатор поста ({@link #id})</li>
 *     <li>Содержимое поста ({@link #content})</li>
 *     <li>Идентификатор автора ({@link #authorId})</li>
 *     <li>Идентификатор проекта ({@link #projectId})</li>
 *     <li>Количество лайков ({@link #totalLikes})</li>
 *     <li>Список идентификаторов комментариев ({@link #commentIds})</li>
 *     <li>Список идентификаторов ресурсов ({@link #resourcesIds})</li>
 *     <li>Идентификатор рекламного объявления ({@link #adId})</li>
 *     <li>Флаг публикации ({@link #published})</li>
 *     <li>Дата публикации ({@link #publishedAt})</li>
 *     <li>Дата запланированной публикации ({@link #scheduledAt})</li>
 *     <li>Флаг удаления поста ({@link #deleted})</li>
 * </ul>
 * </p>
 *
 * @author marsel_mkh
 */
@Data
public class PostViewDto {
    @Schema(description = "Уникальный идентификатор поста",
            example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Содержимое поста",
            example = "content", accessMode = Schema.AccessMode.READ_ONLY)
    private String content;

    @Schema(description = "Идентификатор автора",
            example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long authorId;

    @Schema(description = "Идентификатор проекта",
            example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long projectId;

    @Schema(description = "Количество лайков",
            example = "5", accessMode = Schema.AccessMode.READ_ONLY)
    private Long totalLikes;

    @Schema(description = "Список идентификаторов комментариев",
            example = "[1,2,3]", accessMode = Schema.AccessMode.READ_ONLY)
    private List<Long> commentIds;

    @Schema(description = "Список идентификаторов ресурсов",
            example = "[2,3,4]", accessMode = Schema.AccessMode.READ_ONLY)
    private List<Long> resourcesIds;

    @Schema(description = "Идентификатор рекламного объявления",
            example = "5", accessMode = Schema.AccessMode.READ_ONLY)
    private Long adId;

    @Schema(description = "Флаг публикации",
            example = "true", accessMode = Schema.AccessMode.READ_ONLY)
    private boolean published;

    @Schema(description = "Дата публикации",
            example = "2026-01-01T15:55:00")
    private LocalDateTime publishedAt;

    @Schema(description = "Дата запланированной публикации",
            example = "2026-01-01T15:55:00")
    private LocalDateTime scheduledAt;

    @Schema(description = "Флаг удаления поста",
            example = "true", accessMode = Schema.AccessMode.READ_ONLY)
    private boolean deleted;
}
