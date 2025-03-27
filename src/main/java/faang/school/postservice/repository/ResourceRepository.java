package faang.school.postservice.repository;

import faang.school.postservice.model.Resource;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResourceRepository extends CrudRepository<Resource, Long> {

    @Query(nativeQuery = true, value = "SELECT r.key FROM post_resource r WHERE r.id = :id")
    Optional<String> findResourceKeyById(Long id);

    @Query(nativeQuery = true, value = "SELECT r.type FROM post_resource r WHERE r.id = :id")
    Optional<String> findResourceTypeById(Long id);

    @Modifying
    @Query("DELETE FROM Resource r WHERE r.id = :id")
    void deleteResourceById(Long id);

}
