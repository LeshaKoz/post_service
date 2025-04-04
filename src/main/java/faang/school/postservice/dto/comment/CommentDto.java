package faang.school.postservice.dto.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {

    @Min(value = 1, message = "ID must be greater than or equal to 1.")
    private Long id;

    @NotBlank(message = "Content cannot be blank.")
    @Size(max = 4096, message = "Content cannot exceed 4096 characters.")
    private String content;

    @NotNull(message = "Author ID cannot be null.")
    private Long authorId;

    @NotNull(message = "Post ID cannot be null.")
    private Long postId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}