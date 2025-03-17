package faang.school.postservice.dto.like;

import jakarta.validation.constraints.Positive;
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
    @Positive(message = "userId must be greater than 0")
    private Long userId;
    @Positive(message = "commentId must be greater than 0")
    private Long commentId;
    @Positive(message = "postId must be greater than 0")
    private Long postId;
}
