package faang.school.postservice.dto.post;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record CommentDto(

        Long id,

        @NotNull(message = "Content can't be null")
        @NotEmpty(message = "Content can't be blank")
        String content,

        @NotNull(message = "Author id can't be null")
        @Min(value = 1, message = "Minimum author id is 1")
        long authorId,

        @NotNull(message = "Author id can't be null")
        @Min(value = 1, message = "Minimum author id is 1")
        long postId
) {
}
