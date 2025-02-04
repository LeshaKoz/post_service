package faang.school.postservice.dto.like.comment;

import faang.school.postservice.dto.like.Publication;
import jakarta.validation.constraints.NotNull;


public record LikeCommentDto(

        @NotNull(groups = {After.class})
        Long id,
        @NotNull(groups = {Before.class, After.class})
        Long userId,
        @NotNull(groups = {Before.class, After.class})
        Long postId,
        @NotNull(groups = {Before.class, After.class})
        Long commentId
) implements Publication {
    public interface After {
    }

    public interface Before {
    }
}
