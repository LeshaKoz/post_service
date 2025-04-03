package faang.school.postservice.service.post;

import faang.school.postservice.model.Post;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Aspect
@Component
public class PostCreatedAspect {
    private final PostCreatedAsyncService asyncService;

    @AfterReturning(pointcut = "@annotation(faang.school.aspect.CreatePost)",
            returning = "result")
    public void afterPostCreated(Post result) {
        asyncService.processPostCreated(result);
    }
}
