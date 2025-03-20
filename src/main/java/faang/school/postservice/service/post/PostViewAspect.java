package faang.school.postservice.service.post;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.event.PostViewEvent;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.annotation.ViewPost;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Aspect
@Component
public class PostViewAspect {
    private final EventProducerService publisher;
    private final UserContext userContext;

    @SuppressWarnings("unchecked")
    @AfterReturning(pointcut = "@annotation(faang.school.postservice.service.annotation.ViewPost)", returning = "result")
    public void publishPostView(JoinPoint joinPoint, Object result) {
        Long viewerId = userContext.getUserId();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        ViewPost myAnnotation = method.getAnnotation(ViewPost.class);

        Class<?> resultClass = myAnnotation.targetValue();

        Collection<Post> results = (resultClass == Post.class)
                ? List.of((Post) result)
                : (Collection<Post>) result;

        results.forEach(post -> sendMessageToKafka(post, viewerId));
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