package faang.school.postservice.repository;

import faang.school.postservice.model.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceRepository extends CrudRepository<Resource, Long> {
    List<Resource> findByIdIn(List<Long> ids);
}
