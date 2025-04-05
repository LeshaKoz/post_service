package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PostCreatedEvent {

    @NotNull
    private Long postId;

    @NotNull
    private Long authorId;

    private List<Long> subscriberIds;
}
