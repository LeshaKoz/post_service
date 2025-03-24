package faang.school.postservice.dto.comment;

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
public class CommentViewDto {

    /**
     * Идентификатор комментария.
     */
    private Long id;

    /**
     * Текст комментария.
     */
    private String content;

    /**
     * Идентификатор автора комментария.
     */
    private Long authorId;

    /**
     * Идентификатор поста, к которому относится комментарий.
     */
    private Long postId;

    /**
     * Дата и время создания комментария.
     */
    private LocalDateTime createdAt;

    /**
     * Дата и время последнего обновления комментария.
     */
    private LocalDateTime updatedAt;

    /**
     * Ключ файла большого изображения, если оно прикреплено к комментарию.
     */
    private String largeImageFileKey;

    /**
     * Ключ файла маленького изображения, если оно прикреплено к комментарию.
     */
    private String smallImageFileKey;
}
