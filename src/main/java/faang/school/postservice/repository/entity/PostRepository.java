package faang.school.postservice.repository.entity;

import faang.school.postservice.model.entity.Post;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends CrudRepository<Post, Long> {

    List<Post> findByAuthorId(long authorId);

    List<Post> findByProjectId(long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.projectId = :projectId")
    List<Post> findByProjectIdWithLikes(long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.authorId = :authorId")
    List<Post> findByAuthorIdWithLikes(long authorId);

    @Query("SELECT p FROM Post p WHERE p.published = false AND p.deleted = false AND p.scheduledAt <= CURRENT_TIMESTAMP")
    List<Post> findReadyToPublish();

    @Query("SELECT p FROM Post p " +
            "WHERE p.published= false AND p.deleted = false AND p.authorId = :authorId " +
            "ORDER BY p.createdAt DESC")
    List<Post> findByNotPublishedAndNotDeletedAndAuthorIdOrderCreatedAtDesc(long authorId);

    @Query("SELECT p FROM Post p " +
            "WHERE p.published= false AND p.deleted = false AND p.projectId = :projectId " +
            "ORDER BY p.createdAt DESC")
    List<Post> findByNotPublishedAndNotDeletedAndProjectIdOrderCreatedAtDesc(long projectId);

    @Query("SELECT p FROM Post p " +
            "WHERE p.published= true AND p.deleted = false AND p.authorId = :authorId " +
            "ORDER BY p.createdAt DESC")
    List<Post> findByPublishedAndNotDeletedAndAuthorIdOrderCreatedAtDesc(long authorId);

    @Query("SELECT p FROM Post p " +
            "WHERE p.published= true AND p.deleted = false AND p.projectId = :projectId " +
            "ORDER BY p.createdAt DESC")
    List<Post> findByPublishedAndNotDeletedAndProjectIdOrderCreatedAtDesc(long projectId);

    @Query("SELECT p FROM Post p " +
            "WHERE p.published = false")
    List<Post> findByNotPublished();

    @Query("SELECT p FROM Post p " +
            "WHERE p.verified = false")
    List<Post> findByNotVerified();

    @Query(nativeQuery = true, value = """
        SELECT p.id FROM post p
        JOIN subscription s ON s.followee_id = p.author_id
        JOIN users u ON s.followee_id = u.id
        WHERE s.follower_id = :userId
        ORDER BY p.updated_at DESC
        LIMIT :feedSize""")
    List<Long> getUserFeedPostsIds(Long userId, int feedSize);
}
