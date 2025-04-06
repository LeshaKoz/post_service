package faang.school.postservice.repository.redis;

import faang.school.postservice.model.cache.PostRedis;
import org.springframework.data.repository.CrudRepository;

public interface PostRedisRepository extends CrudRepository<PostRedis, String> {
}
