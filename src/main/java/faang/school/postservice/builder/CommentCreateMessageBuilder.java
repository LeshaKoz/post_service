package faang.school.postservice.builder;

import faang.school.postservice.model.Comment;
import org.springframework.stereotype.Component;

@Component
public class CommentCreateMessageBuilder implements MessageBuilder<Comment> {
    @Override
    public String build(Comment comment) {
        return "postId={" + comment.getPost().getId() + "}"
                + "authorId={" + comment.getAuthorId() + "}"
                + "commentId={" + comment.getId() + "}"
                + "time={" + comment.getCreatedAt() + "}";
    }
}
