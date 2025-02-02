package faang.school.postservice.repository;

import faang.school.postservice.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

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

/*    @Query(value = "SELECT * " +
            "FROM post " +
            "WHERE jsonb_exists(hashtags, :hashtag)", nativeQuery = true)
    List<Post> findPostsByHashtag(@Param("hashtag") String hashtag);*/

/*    @Query("SELECT p.id " +
            "FROM Post p " +
            "WHERE :hashtag MEMBER OF p.hashtags")
    List<Long> findPostIdsByHashtag(@Param("hashtag") String hashtag);*/
/*
    @Query(nativeQuery = true,
            value = """
            SELECT * FROM post
            WHERE hashtags @> CAST(:hashtag AS jsonb)
        """)
    List<Post> findPostsByHashtag(@Param("hashtag") String hashtag);*/

    @Query(nativeQuery = true,
            value = """
           SELECT * FROM post WHERE hashtags @> CAST(:hashtag AS jsonb)
        """)
    List<Post> findPostsByHashtag(@Param("hashtag") String hashtag);

/*    @Query(nativeQuery = true,
            value = """
            SELECT * FROM post
            WHERE hashtags @> '[hashtag]';
            """)
    List<Post> findPostsByHashtag(@Param("hashtag") String hashtag);*/


 /*   @Query(value = "SELECT * " +
            "FROM post " +
            "WHERE hashtags @> :hashtag", nativeQuery = true)
    List<Post> findPostsByHashtag(@Param("hashtag") String hashtag);*/
}