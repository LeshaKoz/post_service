package faang.school.postservice.service.feed;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserRedisDto;
import faang.school.postservice.model.Comment;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
public class AddAuthorCommentToRedisService {

    @Value("${cache.userAuthorTtl}")
    private int userAuthorTtl;
    @Value("${news-feed.keys.user}")
    private String USER_KEY;

    private final HashOperations<String, String, UserRedisDto> hashOperationsUser;
    private final UserServiceClient userServiceClient;

    @Async("asyncTaskExecutor")
    public void addAuthorByCommentToCashAsync(Comment comment) {
        addAuthorByCommentToCash(comment);
    }

    @Transactional
    public void addAuthorByCommentToCash(Comment comment) {

        long authorId = comment.getAuthorId();
        String username = userServiceClient.getUser(authorId).username();

        UserRedisDto userRedisDto = UserRedisDto.builder()
                .id(authorId)
                .username(username)
                .build();

        cacheAuthorByComment(userRedisDto);
    }

    private void cacheAuthorByComment(UserRedisDto userRedisDto) {

        Long authorId = userRedisDto.id();
        hashOperationsUser.put(USER_KEY, authorId.toString(), userRedisDto);

        hashOperationsUser.getOperations().expire(USER_KEY, userAuthorTtl, TimeUnit.SECONDS);
    }
}
