package faang.school.postservice.service.feed;

import faang.school.postservice.dto.user.UserRedisDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
public class UserRedisDtoCashingService {

    @Value("${cache.userAuthorTtl}")
    private int userAuthorTtl;
    @Value("${news-feed.keys.user}")
    private String USER_KEY;

    private final HashOperations<String, String, UserRedisDto> hashOperations;

    public void cacheAuthorByComment(UserRedisDto userRedisDto) {

        Long authorId = userRedisDto.id();
        hashOperations.put(USER_KEY, authorId.toString(), userRedisDto);

        hashOperations.getOperations().expire(USER_KEY, userAuthorTtl, TimeUnit.SECONDS);
    }
}
