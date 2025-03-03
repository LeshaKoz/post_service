package faang.school.postservice.service.like;

import faang.school.postservice.event.LikeEvent;
import faang.school.postservice.model.Like;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Aspect
@Component
public class LikeAspect {

    private final LikeEventPublisher likeEventPublisher;

    @AfterReturning(pointcut = "@annotation(faang.school.postservice.service.like.annotation.AddLike)",
            returning = "result")
    public void addLike(JoinPoint joinPoint, Like result) {
        Object[] args = joinPoint.getArgs();
        Long postId = (Long) args[0];
        Long userId = (Long) args[1];
        LikeEvent event = LikeEvent.builder()
                .postId(postId)
                .authorId(result.getPost().getAuthorId())
                .userId(userId)
                .likeTime(LocalDateTime.now())
                .build();
        likeEventPublisher.publish(event, result.getId());
    }
}
