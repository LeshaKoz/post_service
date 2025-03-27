package faang.school.postservice.dto.feed;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeedPostEvent {
    private long postId;
    private long authorId;
    private LocalDateTime publishedAt;
    private List<Long> subscribersIds;
}
