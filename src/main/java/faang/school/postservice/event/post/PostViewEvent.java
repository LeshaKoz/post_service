package faang.school.postservice.event.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostViewEvent {
    private long postId;
    private long authorId;
    private long viewerId;
    private LocalDateTime viewedAt;
}
