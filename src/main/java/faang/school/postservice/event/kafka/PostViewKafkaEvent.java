package faang.school.postservice.event.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PostViewKafkaEvent {
    private long postId;
    private long userId;
}
