package faang.school.postservice.dto.post;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record HashtagRequestDto(@NotNull @Min(1) Long postId, @NotNull @NotBlank String hashtag) {

}
