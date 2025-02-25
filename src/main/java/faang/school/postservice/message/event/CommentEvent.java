package faang.school.postservice.message.event;

import lombok.Builder;

@Builder
public record CommentEvent(
        long id,
        long postId,
        long authorId,
        String content
) {
}
