package faang.school.postservice.dto.like.post;

import faang.school.postservice.dto.like.Publication;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record LikePostDto(

        @NotNull(groups = {After.class})
        Long id,
        @NotNull(groups = {Before.class, After.class})
        Long userId,
        @NotNull(groups = {Before.class, After.class})
        Long postId
) implements Publication {
    public interface After {
    }

    public interface Before {
    }
}
