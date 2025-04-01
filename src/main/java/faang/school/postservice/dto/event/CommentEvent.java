package faang.school.postservice.dto.event;

import java.time.LocalDateTime;

public record CommentEvent(

        long authorId,

        long postId,

        long postAuthorId,

        long commentId,

        String content,

        LocalDateTime date) {
}
