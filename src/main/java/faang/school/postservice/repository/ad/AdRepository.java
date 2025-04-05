package faang.school.postservice.repository.ad;

import faang.school.postservice.model.ad.Ad;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AdRepository extends CrudRepository<Ad, Long> {

    @Query("SELECT a FROM Ad a WHERE a.post.id = ?1")
    Optional<Ad> findByPostId(long postId);

    List<Ad> findAllByBuyerId(long buyerId);

    List<Ad> findByIdIn(List<Long> ids);

    @Query("SELECT a.id FROM Ad a WHERE a.endDate < :currentDate OR a.appearancesLeft = 0")
    List<Long> findExpiredPostByDateEnd(@Param("currentDate") LocalDateTime currentDate);

    @Modifying
    @Query("DELETE FROM Ad a WHERE a.id = :id")
    void deleteById(@Param("id") @NonNull Long id);
}
