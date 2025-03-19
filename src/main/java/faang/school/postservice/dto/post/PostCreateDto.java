package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
/**
 * Класс `PostCreateDto` используется для передачи данных, необходимых для создания нового поста.
 * Содержит минимальный набор полей, которые должны быть предоставлены при создании поста.
 *
 * <p>Основные поля:
 * <ul>
 *     <li>Содержимое поста ({@link #content})</li>
 *     <li>Идентификатор автора ({@link #authorId})</li>
 *     <li>Идентификатор проекта ({@link #projectId})</li>
 *     <li>Список идентификаторов ресурсов ({@link #resourceIds})</li>
 *     <li>Флаг публикации ({@link #published})</li>
 *     <li>Дата публикации ({@link #publishedAt})</li>
 *     <li>Дата запланированной публикации ({@link #scheduledAt})</li>
 * </ul>
 * </p>
 *
 * @author marsel_mkh
 * @version 1.0
 */
@Data
public class PostCreateDto {
    @NotBlank(message = "Content is not blank")
    private String content;
    private Long authorId;
    private Long projectId;
    private List<Long> resourceIds;
    private boolean published;
    private LocalDateTime publishedAt;
    private LocalDateTime scheduledAt;

}
