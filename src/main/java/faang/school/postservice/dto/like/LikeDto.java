package faang.school.postservice.dto.like;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LikeDto {
    private Long id;
    @Min(value = 1L, message = "postId is negative")
    private Long userId;
    @Min(value = 1L, message = "postId is negative")
    private Long commentId;
    @Min(value = 1L, message = "postId is negative")
    private Long postId;
}
