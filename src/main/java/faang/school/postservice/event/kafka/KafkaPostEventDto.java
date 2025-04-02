package faang.school.postservice.event.kafka;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KafkaPostEventDto extends AbstractKafkaEventDto {
    @NotNull
    private Long postId;
    private List<Long> authorFollowersIds;
    @NotNull
    private Long authorId;

    @Override
    public String getEventId() {
        return "Event_for_post: " + postId;
    }
}
