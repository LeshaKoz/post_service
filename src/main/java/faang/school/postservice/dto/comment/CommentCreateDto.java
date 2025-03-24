package faang.school.postservice.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для создания нового комментария.
 * Содержит данные, необходимые для создания комментария: текст, идентификатор автора и идентификатор поста.
 *
 * @author Zhltsk-V
 * @version 1.0
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentCreateDto {

    /**
     * Текст комментария. Не может быть пустым и должен содержать не более 4096 символов.
     */
    @NotBlank(message = "Текст комментария не может быть пустым")
    @Size(min = 1, max = 4096,
            message = "Текст комментария не может превышать 4096 символов или быть меньше 1 символа")
    private String content;

    /**
     * Идентификатор автора комментария. Должен соответствовать существующему пользователю.
     */
    @NotNull(message = "Идентификатор автора не может быть null")
    private Long authorId;

    /**
     * Идентификатор поста, к которому относится комментарий. Должен соответствовать существующему посту.
     */
    @NotNull(message = "Идентификатор поста не может быть null")
    private Long postId;
}
