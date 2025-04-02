package faang.school.postservice.event.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KafkaPostViewEventDto extends AbstractKafkaEventDto {
    private Long postId;

    @Override
    public String getEventId() {
        return "View_event_for_post: " + postId;
    }
}
