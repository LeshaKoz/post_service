package faang.school.postservice.repository;

import faang.school.postservice.PostCacheDto;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisPostRepository extends CrudRepository<PostCacheDto, Long> {

}
