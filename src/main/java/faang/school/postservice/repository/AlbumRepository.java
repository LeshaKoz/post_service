package faang.school.postservice.repository;

import faang.school.postservice.model.Album;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlbumRepository extends CrudRepository<Album, Long> {
    List<Album> findByIdIn(List<Long> ids);
}
