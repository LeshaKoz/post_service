package faang.school.postservice.repository;

import faang.school.postservice.model.Album;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.stream.Stream;

public interface AlbumRepository extends CrudRepository<Album, Long> {

    Stream<Album> findByAuthorId(long authorId);

    boolean existsByTitleAndAuthorId(String title, long authorId);

    @Query("SELECT a FROM Album a LEFT JOIN FETCH a.posts WHERE a.id = :id")
    Optional<Album> findByIdWithPosts(long id);

    @Query(nativeQuery = true, value = """
            INSERT INTO favorite_albums (album_id, user_id)
            SELECT :albumId, :userId
            WHERE NOT EXISTS (
                SELECT 1 FROM favorite_albums WHERE album_id = :albumId AND user_id = :userId
            )
            """)
    @Modifying
    void addAlbumToFavorites(long albumId, long userId);

    @Query(nativeQuery = true, value = "DELETE FROM favorite_albums WHERE album_id = :albumId AND user_id = :userId")
    @Modifying
    void deleteAlbumFromFavorites(long albumId, long userId);

    @Query(nativeQuery = true, value = """
            SELECT * FROM album
            WHERE id IN (
                SELECT album_id FROM favorite_albums WHERE user_id = :userId
            )
            """)
    Stream<Album> findFavoriteAlbumsByUserId(long userId);
}
