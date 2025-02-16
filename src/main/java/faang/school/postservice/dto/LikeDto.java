package faang.school.postservice.dto;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LikeDto {
    private Long id;
    @NotNull
    private Long userId;
    private Long commentId;
    private Long postId;
}