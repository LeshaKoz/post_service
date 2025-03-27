package faang.school.postservice.repository.album;

import faang.school.postservice.model.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AlbumRepository extends JpaRepository<Album, Long> {

    List<Album> findAlbumByAuthorId(long authorId);

    Album findAlbumById(long id);

    @Query(nativeQuery = true, value = "INSERT INTO favorite_albums (album_id, user_id) VALUES (:albumId, :userId)")
    void addAlbumToFavorite(long albumId, long userId);

    @Query(nativeQuery = true, value = "DELETE FROM favorite_albums WHERE user_id = :userId AND album_id = :albumId")
    void deleteAlbumFromFavorite(long albumId, long userId);

    @Query(nativeQuery = true, value = "SELECT album_id FROM favorite_albums WHERE user_id = :userId")
    long[] findFavoriteAlbumIdsByUserId(long userId);

    void deleteAlbumById(long id);
}
