package faang.school.postservice.dto.comment;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CommentDto {
    private Long id;

    @Positive
    private Long authorId;

    @Positive
    private Long postId;

    @NotBlank
    @Size(max = 4096)
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}