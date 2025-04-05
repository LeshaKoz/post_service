package faang.school.postservice.service.feed;

import faang.school.postservice.dto.user.UserRedisDto;
import faang.school.postservice.model.Comment;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserRedisDtoProcessingService {
    private final UserRedisDtoDataService userRedisDtoDataService;
    private final UserRedisDtoCashingService cacheAuthorByComment;

    @Transactional
    public void processAndCacheAuthorByComment (Comment comment) {

        UserRedisDto userRedisDto = userRedisDtoDataService.fetchUserInfo(comment);
        cacheAuthorByComment.cacheAuthorByComment(userRedisDto);
    }

}
