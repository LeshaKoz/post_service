package faang.school.postservice.repository.cache;

import faang.school.postservice.model.cache.CacheAuthor;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CacheAuthorRepository extends ListCrudRepository<CacheAuthor, String> {
}
