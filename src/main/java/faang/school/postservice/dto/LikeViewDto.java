package faang.school.postservice.dto;

import lombok.Data;

/**
 * DTO для представления информации о лайке.
 * <p>
 * Поля класса:
 * <ul>
 *   <li><b>id</b> - уникальный идентификатор лайка</li>
 *   <li><b>userId</b> - идентификатор пользователя, поставившего лайк</li>
 *   <li><b>commentId</b> - идентификатор комментария, если лайк для комментария (может быть null)</li>
 *   <li><b>postId</b> - идентификатор поста, если лайк для поста (может быть null)</li>
 * </ul>
 * <p>
 * Лайк может относиться либо к посту, либо к комментарию, поэтому одно из полей
 * commentId/postId всегда должно быть заполнено.
 *
 * @author gulnaz21
 */
@Data
public class LikeViewDto {
    private long id;
    private Long userId;
    private Long commentId;
    private Long postId;
}
