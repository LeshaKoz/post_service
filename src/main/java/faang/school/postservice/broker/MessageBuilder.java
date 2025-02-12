package faang.school.postservice.broker;

import org.springframework.stereotype.Component;

@Component
public class MessageBuilder {
    public String generateLikeEventMessage(Long authorId, Long likerId, Long postId) {
        return "authorId={" + authorId + "} "
                + "likerId={" + likerId + "} "
                + "postId={" + postId + "}";
    }
}
