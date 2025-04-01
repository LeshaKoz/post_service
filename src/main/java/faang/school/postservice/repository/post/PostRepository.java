package faang.school.postservice.repository.post;

import faang.school.postservice.model.post.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByAuthorId(long authorId);

    List<Post> findByProjectId(long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.projectId = :projectId")
    List<Post> findByProjectIdWithLikes(long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.authorId = :authorId")
    List<Post> findByAuthorIdWithLikes(long authorId);

    @Query("SELECT p FROM Post p WHERE p.published = false AND p.deleted = false AND p.scheduledAt <= CURRENT_TIMESTAMP")
    List<Post> findReadyToPublish();

    @Query("SELECT p FROM Post p JOIN p.hashtags h WHERE h.name = :hashtag ORDER BY p.createdAt DESC")
    List<Post> findByHashtag(@Param("hashtag") String hashtag);

    List<Post> findByVerified(boolean isVerified);

    @Query(nativeQuery = true, value = """
            SELECT p.* FROM post p
            JOIN subscription s ON p.author_id = s.followee_id
            WHERE s.follower_id = :userId
            AND (:after IS NULL OR p.created_at < :after)
            ORDER BY p.created_at DESC
            LIMIT :limit
            """)
    List<Post> findForUserFeed(long userId, Instant after, int limit);

    @Query("SELECT p FROM Post p WHERE p.authorId IN :authorIds ORDER BY p.createdAt DESC")
    List<Post> findLatestByAuthorIds(List<String> authorIds, Pageable pageable);

}
