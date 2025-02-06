package faang.school.postservice.dto.post;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@Schema
public class PostReadDto {
    @Schema(
            description = "Идентификатор поста",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;
    @Schema(
            description = "Содержимое поста",
            example = "Круто!",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private String content;
    @Schema(
            description = "Идентификатор пользователя, создавшего пост",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long authorId;
    @Schema(
            description = "Идентификатор проекта, создавшего пост",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long projectId;
    @Schema(
            description = "Количество лайков под постом",
            example = "21",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Integer likesCount;
    @Schema(
            description = "Время создания",
            example = "2021-10-10T10:00:00",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private LocalDateTime createdAt;
    @Schema(
            description = "Опубликован ли пост",
            example = "true",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private boolean published;
    @Schema(
            description = "Удален ли пост",
            example = "false",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private boolean deleted;
    @Schema(
            description = "Время публикации",
            example = "2021-10-10T10:00:00",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private LocalDateTime publishedAt;
}
