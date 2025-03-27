package faang.school.postservice.repository.ad;

import faang.school.postservice.model.ad.Ad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AdRepository extends JpaRepository<Ad, Long> {

    @Query("SELECT a FROM Ad a WHERE a.post.id = ?1")
    Optional<Ad> findByPostId(long postId);

    List<Ad> findAllByBuyerId(long buyerId);

    @Query("SELECT a FROM Ad a WHERE (a.endDate < :currentDateTime OR a.appearancesLeft = 0)")
    List<Ad> findExpiredAds(@Param("currentDateTime") LocalDateTime currentDateTime);
}