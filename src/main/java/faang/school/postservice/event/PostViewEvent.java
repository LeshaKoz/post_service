package faang.school.postservice.event;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.joda.time.LocalDateTime;

@RequiredArgsConstructor
@Builder
public class PostViewEvent {

    @NotNull
    @Positive
    private final Long postId;

    @NotNull
    @Positive
    private final Long authorId;

    @NotNull
    @Positive
    private final Long userId;

    private final LocalDateTime whenViewed;
}
