package faang.school.postservice.validator.album;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class AlbumValidatorTest {

    AlbumDto albumDto = new AlbumDto();
    Album album = new Album();

    void prepareAlbumData() {
        album.setTitle("Title");
        album.setDescription("description");
        album.setAuthorId(1L);
        album.setPosts(new ArrayList<>());
    }

    @Test
    void testThrowExceptionWhenTitleIsEmpty() {
        albumDto.setTitle("");
        albumDto.setDescription("description");
        assertThrows(DataValidationException.class, () -> AlbumValidator.checkAlbumDtoTitleAndDescriptionExist(albumDto));
    }

    @Test
    void testThrowExceptionWhenDescriptionIsEmpty() {
        albumDto.setTitle("Valid title");
        albumDto.setDescription("");
        assertThrows(DataValidationException.class, () -> AlbumValidator.checkAlbumDtoTitleAndDescriptionExist(albumDto));
    }

    @Test
    void checkUserExist_shouldThrowException_whenUserNotFound() {
        UserDto userDto = new UserDto(2L, "Name", "email");

        assertThrows(EntityNotFoundException.class, () -> AlbumValidator.checkUserExist(1L, userDto));
    }

    @Test
    void testThrowExceptionWhenAlbumExists() {
        prepareAlbumData();
        List<Album> albums = List.of(album);

        assertThrows(IllegalArgumentException.class, () -> AlbumValidator.checkAlbumNotExist("Title", albums));
    }

    @Test
    void testThrowExceptionWhenUserIsNotAuthor() {
        prepareAlbumData();

        assertThrows(DataValidationException.class, () -> AlbumValidator.checkAlbumAuthorWithUser(2L, album));
    }

    @Test
    void testThrowExceptionWhenPostAlreadyExistsInAlbum() {
        Post post = new Post();
        post.setContent("content");
        post.setAuthorId(2L);
        prepareAlbumData();
        album.addPost(post);

        assertThrows(IllegalArgumentException.class, () -> AlbumValidator.checkPostInAlbum(post, album));
    }
}
