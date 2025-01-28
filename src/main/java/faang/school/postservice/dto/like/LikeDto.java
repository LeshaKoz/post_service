package faang.school.postservice.dto.like;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class LikeDto {
    @PositiveOrZero
    @NotNull
    private Long Id;

    @PositiveOrZero
    @NotNull
    private final Long userId;

    @PositiveOrZero
    @NotNull
    private final Long commentId;

    @PositiveOrZero
    @NotNull
    private final Long postId;
}
