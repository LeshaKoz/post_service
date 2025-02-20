package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreatePostDto {

    @NotNull(message = "Автор или проект должны быть указаны")
    @Positive
    private Long authorId;

    @Positive
    private Long projectId;

    @Size(min = 1, max = 4096, message = "Контент не может быть пустым и не должен превышать 4096 символов")
    private String content;

    private Boolean published;
}