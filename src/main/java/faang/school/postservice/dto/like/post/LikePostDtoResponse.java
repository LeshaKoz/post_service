package faang.school.postservice.dto.like.post;

import jakarta.validation.constraints.NotNull;

public record LikePostDtoResponse(

        @NotNull(groups = {After.class})
        Long id,
        @NotNull(groups = {Before.class, After.class})
        Long userId,
        @NotNull(groups = {Before.class, After.class})
        Long postId
) {
    public interface After {
    }

    public interface Before {
    }
}
