package faang.school.postservice.dto.feed;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedRedisDto {
    private Long userId;
    private List<Long> postId;
}
