package faang.school.postservice.repository;

import faang.school.postservice.enums.KafkaStatus;
import faang.school.postservice.model.KafkaMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface KafkaMessageRepository extends JpaRepository<KafkaMessage, Long> {

    List<KafkaMessage> findByStatusOrderByCreatedAt(KafkaStatus status);

    @Modifying
    @Query("""
            UPDATE KafkaMessage m
            SET m.status = :status, m.attempts = m.attempts + 1, m.updatedAt = CURRENT_TIMESTAMP
            WHERE m.id = :id
            """)
    void updateStatusAndIncrementAttempts(long id, KafkaStatus status);

    @Modifying
    @Query("UPDATE KafkaMessage m SET m.status = :status WHERE m.id = :id")
    void updateStatus(long id, KafkaStatus status);
}
