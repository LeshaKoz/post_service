package faang.school.postservice.service.post;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.event.PostViewEvent;
import faang.school.postservice.model.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Aspect
@Component
public class PostViewAspect {
    private final PostViewEventPublisher publisher;
    private final UserContext userContext;

    @AfterReturning(pointcut = "@annotation(faang.school.postservice.service.annotation.ViewPost)", returning = "result")
    public void publishPostView(Object result) {
        Long viewerId = userContext.getUserId();

        if (result instanceof Post post) {
            sendMessageToKafka(post, viewerId);
            return;
        }

        if (result instanceof List<?> list) {
            if (!list.isEmpty() && list.get(0) instanceof Post) {
                List<Post> posts = list.stream()
                        .filter(Post.class::isInstance)
                        .map(Post.class::cast)
                        .toList();

                posts.forEach(post -> sendMessageToKafka(post, viewerId));
            }
        }
    }

    private void sendMessageToKafka(Post post, Long viewerId) {
        Long authorId = post.getAuthorId();

        if (!Objects.equals(viewerId, authorId)) {
            PostViewEvent event = PostViewEvent.builder()
                    .postId(post.getId())
                    .authorId(authorId)
                    .userId(viewerId)
                    .build();

            publisher.publish(event);
            log.info("User {} viewed post {}", viewerId, post.getId());
        }
    }
}