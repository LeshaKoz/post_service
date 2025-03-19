package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Класс `PostUpdateDto` используется для передачи данных, необходимых для обновления существующего поста.
 * Содержит минимальный набор полей, которые могут быть изменены при обновлении поста.
 *
 * <p>Основные поля:
 * <ul>
 *     <li>Содержимое поста ({@link #content})</li>
 *     <li>Идентификатор автора ({@link #authorId})</li>
 * </ul>
 * </p>
 *
 * @author marsel_mkh
 */
@Data
public class PostUpdateDto {
    @NotBlank(message = "Content is not blank")
    private String content;
    @NotNull(message = "Author can not be null")
    private Long authorId;
}
