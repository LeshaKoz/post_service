package faang.school.postservice.repository.redis;

import faang.school.postservice.model.cache.FeedCache;
import org.springframework.data.repository.ListCrudRepository;

public interface RedisFeedRepository extends ListCrudRepository<FeedCache, Long> {
}
