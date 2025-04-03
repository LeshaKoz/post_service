package faang.school.postservice.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentEvent {
    @NotNull(message = "ID поста не может быть null")
    private Long postId;

    @NotNull(message = "ID коммента не может быть null")
    private Long commentId;

    @NotNull(message = "ID автора поста не может быть null")
    private Long authorId;

    @NotBlank(message = "Коммент не может быть пустым")
    private String content;
}
