package faang.school.postservice.repository;

import faang.school.postservice.dto.feed.FeedRedisDto;
import faang.school.postservice.dto.user.UserRedisDto;
import faang.school.postservice.model.Post;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByAuthorId(long authorId);

    List<Post> findByProjectId(long projectId);

    @Query("SELECT p " +
        "FROM Post p " +
        "LEFT JOIN FETCH p.likes " +
        "WHERE p.projectId = :projectId")
    List<Post> findByProjectIdWithLikes(long projectId);

    @Query("SELECT p " +
        "FROM Post p " +
        "LEFT JOIN FETCH p.likes " +
        "WHERE p.authorId = :authorId")
    List<Post> findByAuthorIdWithLikes(long authorId);

    @Query("SELECT p " +
        "FROM Post p " +
        "WHERE p.published = false AND p.deleted = false AND p.scheduledAt <= CURRENT_TIMESTAMP")
    List<Post> findReadyToPublish();

    @Query(value = "SELECT id,username, email from users", nativeQuery = true)
    List<Object[]> getUsers();

}