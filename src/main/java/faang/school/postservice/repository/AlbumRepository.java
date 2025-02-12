package faang.school.postservice.repository;

import faang.school.postservice.model.Album;
import feign.Param;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AlbumRepository extends JpaRepository<Album, Long> {

    Optional<Album> findById(long id);

    List<Album> findByAuthorId(long authorId);

    boolean existsByTitleAndAuthorId(String title, long authorId);

    @Query("SELECT a FROM Album a LEFT JOIN FETCH a.posts WHERE a.id = :id")
    Optional<Album> findByIdWithPosts(@Param("id") long id);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO favorite_albums (album_id, user_id) VALUES (:albumId, :userId)", nativeQuery = true)
    void addAlbumToFavorites(@Param("albumId") long albumId, @Param("userId") long userId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM favorite_albums WHERE album_id = :albumId AND user_id = :userId", nativeQuery = true)
    void deleteAlbumFromFavorites(@Param("albumId") long albumId, @Param("userId") long userId);

    @Query(value = "SELECT * FROM album WHERE id IN (SELECT album_id FROM favorite_albums WHERE user_id = :userId)",
            nativeQuery = true)
    List<Album> findFavoriteAlbumsByUserId(@Param("userId") long userId);

    @Query(value = "SELECT EXISTS (SELECT 1 FROM favorite_albums WHERE album_id = :albumId AND user_id = :userId)",
            nativeQuery = true)
    boolean isFavorite(@Param("albumId") long albumId, @Param("userId") long userId);

}
