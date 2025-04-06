package faang.school.postservice.event.post;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PostDeletedEvent {
    private Long postId;
    private List<Long> followerIds;
}
