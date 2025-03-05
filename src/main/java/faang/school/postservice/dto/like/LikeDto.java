package faang.school.postservice.dto.like;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Validated
public class LikeDto {
    private Long id;
    @Min(value = 1L, message = "userId must be greater than 0")
    private Long userId;
    @Min(value = 1L, message = "commentId must be greater than 0")
    private Long commentId;
    @Min(value = 1L, message = "postId must be greater than 0")
    private Long postId;
}
