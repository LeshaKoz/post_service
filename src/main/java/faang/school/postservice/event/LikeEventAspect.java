package faang.school.postservice.event;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Aspect
@Component
public class LikeEventAspect {

    private final EventPublisher eventPublisher;
    private final PostRepository postRepository;

    @Around("@annotation(PublishLikeEvent)")
    public Object publishEvent(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();

        Object[] args = joinPoint.getArgs();
        if (args.length < 3) {
            log.warn("Invalid method signature for LikeEvent publishing");
            return result;
        }

        Long postId = (Long) args[0];
        Long userId = (Long) args[2];

        Optional.ofNullable(getAuthorIdFromPost(postId))
                .ifPresent(authorId -> {
                    Event event = new LikeEvent(postId, userId, authorId);
                    eventPublisher.publishEvent("like-events", event);
                    log.info("Published LikeEvent: {}", event);
                });

        return result;
    }

    private Long getAuthorIdFromPost(Long postId) {
        return postRepository.findById(postId)
                .map(Post::getAuthorId)
                .orElse(null);
    }
}