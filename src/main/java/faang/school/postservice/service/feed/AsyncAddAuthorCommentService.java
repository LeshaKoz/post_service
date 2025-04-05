package faang.school.postservice.service.feed;

import faang.school.postservice.model.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AsyncAddAuthorCommentService {
    private final UserRedisDtoProcessingService userRedisDtoProcessingService;

    @Async("asyncTaskExecutor")
    public void addAuthorByCommentToCashAsync(Comment comment) {
        userRedisDtoProcessingService.processAndCacheAuthorByComment(comment);
    }
}
