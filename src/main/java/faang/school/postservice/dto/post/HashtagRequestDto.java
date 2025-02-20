package faang.school.postservice.dto.post;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record HashtagRequestDto(
        @NotNull(message = "Post id can't be null")
        @Min(value = 1, message = "Minimum post id is 1")
        Long postId,

        @NotNull(message = "Hashtag not be null")
        @NotBlank(message = "Hashtag not be empty")
        String hashtag) {

}
