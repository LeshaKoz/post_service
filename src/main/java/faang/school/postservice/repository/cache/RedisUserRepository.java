package faang.school.postservice.repository.cache;

import faang.school.postservice.model.cache.UserCache;
import org.springframework.data.keyvalue.repository.KeyValueRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisUserRepository extends KeyValueRepository<UserCache, Long> {
}
