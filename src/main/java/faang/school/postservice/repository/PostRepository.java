package faang.school.postservice.repository;

import faang.school.postservice.model.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PostRepository extends CrudRepository<Post, Long>, JpaSpecificationExecutor<Post> {

    List<Post> findByAuthorId(long authorId);

    List<Post> findByProjectId(long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.projectId = :projectId")
    List<Post> findByProjectIdWithLikes(long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.authorId = :authorId")
    List<Post> findByAuthorIdWithLikes(long authorId);

    @Query("SELECT p FROM Post p WHERE p.published = false AND p.deleted = false AND p.scheduledAt <= CURRENT_TIMESTAMP")
    List<Post> findReadyToPublish();

    @Query(value = "SELECT * FROM post p " +
            "WHERE (:authorIds IS NULL OR p.author_id IN (:authorIds)) " +
            "AND p.deleted = false " +
            "AND (:lastSeenDate IS NULL OR p.published_at < :lastSeenDate) " +
            "ORDER BY p.published_at DESC " +
            "LIMIT :quantity", nativeQuery = true)
    List<Post> findPostsForFeed(@Param("authorIds") List<Long> authorIds,
                                @Param("lastSeenDate") LocalDateTime lastSeenDate,
                                @Param("quantity") int quantity);

    /*
    @Query(value = "SELECT * FROM post p " +
            "WHERE p.author_id IN (:authorIds) " +
            "AND p.deleted = false " +
            "AND (:lastSeenDate IS NULL OR p.published_at < :lastSeenDate)" +
            "ORDER BY p.published_at DESC " +
            "LIMIT :quantity", nativeQuery = true)
    List<Post> findPostsForFeed(List<Long> authorIds, LocalDateTime lastSeenDate, int quantity); */

    /*@Query("SELECT p FROM Post p " +
            "WHERE p.authorId IN :authorIds " +
            "AND p.deleted = false " +
            "AND (:lastSeenDate IS NULL OR p.publishedAt < :lastSeenDate) " +
            "ORDER BY p.publishedAt DESC")
    List<Post> findPostsForFeed(@Param("authorIds") List<Long> authorIds,
                                @Param("lastSeenDate") LocalDateTime lastSeenDate,
                                Pageable pageable); */
}
