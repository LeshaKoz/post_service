package faang.school.postservice.event;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;

@Getter
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

    @Builder.Default
    private final LocalDateTime viewedAt = LocalDateTime.now();
}