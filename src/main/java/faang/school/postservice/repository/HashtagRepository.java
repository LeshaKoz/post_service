package faang.school.postservice.repository;

import faang.school.postservice.model.Hashtag;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface HashtagRepository extends CrudRepository<Hashtag, Long> {
    List<Hashtag> findById(long id);
    Optional<Hashtag> findByName(String name);
}
