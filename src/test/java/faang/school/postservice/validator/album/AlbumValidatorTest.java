package faang.school.postservice.validator.album;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.ForbiddenException;
import faang.school.postservice.exception.UnauthorizedException;
import faang.school.postservice.model.Album;
import faang.school.postservice.repository.AlbumRepository;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlbumValidatorTest {
    @Mock
    private AlbumRepository albumRepository;
    @Mock
    private UserServiceClient userServiceClient;
    @InjectMocks
    private AlbumValidatorImpl albumValidator;

    @Test
    void testValidateUserExists_whenUserIsAbsent() {
        long userId = 1L;
        when(userServiceClient.getUser(userId)).thenThrow(FeignException.NotFound.class);

        assertThrows(UnauthorizedException.class, () -> albumValidator.validateUserExists(userId));

        verify(userServiceClient, times(1)).getUser(userId);
    }

    @Test
    void testValidateUserExistsSuccessfully() {
        long userId = 1L;

        assertDoesNotThrow(() -> albumValidator.validateUserExists(userId));

        verify(userServiceClient, times(1)).getUser(userId);
    }

    @Test
    void testValidateTitle_whenTitleIsNotUnique() {
        long userId = 1L;
        String title = "Title";
        when(albumRepository.existsByTitleAndAuthorId(title, userId)).thenReturn(true);

        assertThrows(DataValidationException.class, () -> albumValidator.validateTitle(title, userId));

        verify(albumRepository, times(1)).existsByTitleAndAuthorId(title, userId);
    }

    @Test
    void testValidateTitleSuccessfully() {
        long userId = 1L;
        String title = "Title";
        when(albumRepository.existsByTitleAndAuthorId(title, userId)).thenReturn(false);

        assertDoesNotThrow(() -> albumValidator.validateTitle(title, userId));

        verify(albumRepository, times(1)).existsByTitleAndAuthorId(title, userId);
    }

    @Test
    void validateAuthor_whenUserIsNotAuthor() {
        long userId = 1L;
        long authorId = 2L;
        Album album = new Album();
        album.setAuthorId(authorId);

        assertThrows(ForbiddenException.class, () -> albumValidator.validateAuthor(album, userId));
    }

    @Test
    void validateAuthorSuccessfully() {
        long userId = 1L;
        long authorId = 1L;
        Album album = new Album();
        album.setAuthorId(authorId);

        assertDoesNotThrow(() -> albumValidator.validateAuthor(album, userId));
    }
}
