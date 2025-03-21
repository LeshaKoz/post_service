package faang.school.postservice.dto.comment;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {

    private Long id;

    @Size(max = 4096, message = "Comment can be maximal 4096 characters")
    private String content;

    @Positive(message = "Comment must have authorId")
    private Long authorId;
    private Long countOfLikes;
    @Positive(message = "Comment must have postId")
    private Long postId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String largeImageFileKey;
    private String smallImageFileKey;
}