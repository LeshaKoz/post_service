package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.util.List;

@Builder
public record CreatePostEvent(
        @NotNull @Positive Long postId,
        @NotNull List<Long> followers
) {
}
