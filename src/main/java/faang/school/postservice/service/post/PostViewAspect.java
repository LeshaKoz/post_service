package faang.school.postservice.service.post;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.ResponsePostDto;
import faang.school.postservice.event.PostViewEvent;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.joda.time.LocalDateTime;
import org.springframework.stereotype.Component;

import java.util.Objects;

@RequiredArgsConstructor
@Aspect
@Component
public class PostViewAspect {
    private final PostViewEventPublisher publisher;
    private final UserContext userContext;

    @AfterReturning(pointcut = "@annotation(ViewPost)", returning = "result")
    public void publishPostView(ResponsePostDto dto) {
        Long viewerId = userContext.getUserId();
        Long authorId = dto.getAuthorId();

        if (!Objects.equals(viewerId, authorId)) {
            PostViewEvent event = PostViewEvent.builder()
                    .postId(dto.getId())
                    .authorId(authorId)
                    .userId(viewerId)
                    .whenViewed(LocalDateTime.now())
                    .build();

            publisher.publish(event);
        }
    }
}