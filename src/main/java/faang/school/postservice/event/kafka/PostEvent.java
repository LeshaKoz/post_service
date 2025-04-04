package faang.school.postservice.event.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostEvent {
    private Long postId;
    private Long authorId;
    private List<Long> subscriberIds;
}
