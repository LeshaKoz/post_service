package faang.school.postservice.dto.comment;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CommentDto(
        long id,
        long authorId,
        long postId,
        String content,
        LocalDateTime createdAt
) {
}
