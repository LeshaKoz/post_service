package faang.school.postservice.repository.redis;

import faang.school.postservice.dto.user.UserRedisDto;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Repository;

@Repository
public class RedisUserRepository {
    @Value("${news-feed.keys.user}")
    private String USER_KEY;

    private final HashOperations<String, String, UserRedisDto> hashOperationsUser;

    public RedisUserRepository(
        @Qualifier("userRedis") HashOperations<String, String, UserRedisDto> hashOperationsUser) {
        this.hashOperationsUser = hashOperationsUser;
    }

    public boolean checkUserExist(Long userId) {
        return hashOperationsUser.hasKey(USER_KEY, userId);
    }

    public List<UserRedisDto> getUsers(List<Long> authorIds) {
        List<String> authors = authorIds.stream().map(String::valueOf).toList();
        return hashOperationsUser.multiGet(USER_KEY, authors);
    }
}
