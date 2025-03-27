package faang.school.postservice.repository;

import faang.school.postservice.dto.user.AuthorCacheDto;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisAuthorRepository extends CrudRepository<AuthorCacheDto, Long> {
}
