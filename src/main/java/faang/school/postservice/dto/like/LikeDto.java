package faang.school.postservice.dto.like;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LikeDto {

    private Long userId;
    private Long commentId;
    private Long postId;
}
