package faang.school.postservice.event;

import lombok.Data;

import java.util.List;

@Data
public class PostCreatedEvent {
    private Long postId;
    private Long authorId;
    private List<Long> followerIds;
}
