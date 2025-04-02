package faang.school.postservice.repository.cache;

import faang.school.postservice.model.cache.CachePost;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CachePostRepository extends ListCrudRepository<CachePost, Long> {
}
