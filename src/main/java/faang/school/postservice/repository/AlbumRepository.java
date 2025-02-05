package faang.school.postservice.repository;

import faang.school.postservice.model.Album;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface AlbumRepository extends CrudRepository<Album, Long> {

    List<Album> findByAuthorId(long authorId);

    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN TRUE ELSE FALSE END " +
            "FROM Album a " +
            "WHERE a.title = :title AND a.authorId = :authorId")
    boolean existsByTitleAndAuthorId(String title, long authorId);

    @Query("SELECT a FROM Album a LEFT JOIN FETCH a.posts WHERE a.id = :id")
    List<Album> findByIdWithPosts(long id);

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO favorite_albums (album_id, user_id) VALUES (:albumId, :userId)", nativeQuery = true)
    void addAlbumToFavorites(long albumId, long userId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM favorite_albums WHERE album_id = :albumId AND user_id = :userId", nativeQuery = true)
    void deleteAlbumFromFavorites(long albumId, long userId);

    @Query(value = "SELECT album_id FROM favorite_albums WHERE user_id = :userId", nativeQuery = true)
    List<Album> findFavoriteAlbumsByUserId(long userId);
}
