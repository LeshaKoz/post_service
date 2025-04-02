package faang.school.postservice.repository.redis;

import faang.school.postservice.model.cache.PostCache;
import org.springframework.data.repository.ListCrudRepository;

public interface RedisPostRepository extends ListCrudRepository<PostCache, Long> {
}
