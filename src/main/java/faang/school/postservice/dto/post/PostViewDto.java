package faang.school.postservice.dto.post;

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
 *     <li>Список идентификаторов лайков ({@link #likeIds})</li>
 *     <li>Список идентификаторов комментариев ({@link #commentIds})</li>
 *     <li>Список идентификаторов ресурсов ({@link #resourceIds})</li>
 *     <li>Идентификатор рекламного объявления ({@link #adId})</li>
 *     <li>Флаг публикации ({@link #published})</li>
 *     <li>Дата публикации ({@link #publishedAt})</li>
 *     <li>Дата запланированной публикации ({@link #scheduledAt})</li>
 * </ul>
 * </p>
 *
 * @author marsel_mkh
 */
@Data
public class PostViewDto {
    private Long id;
    private String content;
    private Long authorId;
    private Long projectId;
    private List<Long> likeIds;
    private List<Long> commentIds;
    private List<Long> resourceIds;
    private Long adId;
    private boolean published;
    private LocalDateTime publishedAt;
    private LocalDateTime scheduledAt;
    private boolean deleted;

}
