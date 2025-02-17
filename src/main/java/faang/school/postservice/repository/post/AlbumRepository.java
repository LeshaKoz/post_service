package faang.school.postservice.repository.post;

import faang.school.postservice.model.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AlbumRepository extends JpaRepository<Album, Long> {

    List<Album> findByAuthorId(long id);

    @Query(nativeQuery = true, value = "SELECT user_id FROM album_allowed_users WHERE album_id = :id")
    List<Long> findSelectedUsersForAlbum(long id);

    @Query(nativeQuery = true, value = """
            INSERT INTO album_allowed_users (album_id, user_id)
            VALUES (:albumId, :userId)
            ON CONFLICT (album_id, user_id) DO NOTHING
            """)
    @Modifying
    void addUserForVisibilityAtAlbum(long albumId, long userId);

    @Query(nativeQuery = true, value = """
            INSERT INTO favorite_albums (album_id, user_id)
            VALUES (:albumId, :userId)
            ON CONFLICT (album_id, user_id) DO NOTHING
            """)
    @Modifying
    void addAlbumToFavorites(long albumId, long userId);

    @Query(nativeQuery = true, value = """
            DELETE FROM favorite_albums
            WHERE album_id = :albumId AND user_id = :userId
            """)
    @Modifying
    void deleteAlbumFromFavorites(long albumId, long userId);


    @Query(nativeQuery = true, value = """
            SELECT a.* FROM albums a
            JOIN favorite_albums fa ON fa.album_id = a.id
            WHERE fa.user_id = :userId
            """)
    List<Album> findFavoritesByAuthorId(long userId);
}
