package faang.school.postservice.repository;

import faang.school.postservice.dto.user.PostAuthorCacheDto;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisPostAuthorRepository extends CrudRepository<PostAuthorCacheDto, Long> {
}
