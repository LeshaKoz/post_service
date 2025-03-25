package faang.school.postservice.repository.redis;

import faang.school.postservice.model.cache.UserCache;
import org.springframework.data.repository.ListCrudRepository;

public interface RedisUserRepository extends ListCrudRepository<UserCache, Long> {
}
