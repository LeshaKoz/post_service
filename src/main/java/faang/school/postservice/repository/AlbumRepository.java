package faang.school.postservice.repository;

import faang.school.postservice.model.Album;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {

    boolean existsByTitleAndAuthorId(String title, long authorId);

    List<Album> findByAuthorId(long authorId);

    @Query("SELECT a FROM Album a LEFT JOIN FETCH a.posts WHERE a.id = :id")
    Optional<Album> findByIdWithPosts(long id);

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO favorite_albums (album_id, user_id) VALUES (?1, ?2)", nativeQuery = true)
    void addAlbumToFavorites(long albumId, long userId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM favorite_albums WHERE album_id = ?1 AND user_id = ?2", nativeQuery = true)
    void deleteAlbumFromFavorites(long albumId, long userId);

    @Query(value = """
            SELECT a.* FROM album a
            JOIN favorite_albums f ON a.id = f.album_id
            WHERE f.user_id = ?1
            """, nativeQuery = true)
    List<Album> findFavoriteAlbumsByUserId(long userId);
}
