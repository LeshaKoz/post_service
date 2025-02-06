package faang.school.postservice.dto.like;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LikeDto {

    @NotNull
    private Long userId;

    @NotNull
    private Long postId;

    @NotNull
    private Long commentId;
}