package faang.school.postservice.dto;

import java.util.List;

public record CommentDto(
        long id,
        String content,
        long authorId,
        long amountLikes,
        long postId
) {
}
