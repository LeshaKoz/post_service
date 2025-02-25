package faang.school.postservice.event;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
@Builder
public class PostViewEvent {

    private final Long postId;
    private final Long authorId;
    private final Long userId;

    @Builder.Default
    private final LocalDateTime viewedAt = LocalDateTime.now();
}