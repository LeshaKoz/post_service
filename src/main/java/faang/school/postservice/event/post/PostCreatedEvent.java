package faang.school.postservice.event.post;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
public class PostCreatedEvent {
    private Long postId;
    private Long authorId;
    private List<Long> followerIds;
    private boolean deleted;
    private String content;

    @CreationTimestamp
    private Instant createdAt;
}
