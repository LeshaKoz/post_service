package faang.school.postservice.dto.comment;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentUpdateDto {

    @Min(value = 1, message = "ID must be greater than or equal to 1.")
    private Long id;

    @NotBlank(message = "Content cannot be blank.")
    @Size(max = 4096, message = "Content cannot exceed 4096 characters.")
    private String content;

    @NotNull(message = "Author ID cannot be null.")
    private Long authorId;
}