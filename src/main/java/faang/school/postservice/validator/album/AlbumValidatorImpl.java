package faang.school.postservice.validator.album;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.ForbiddenException;
import faang.school.postservice.exception.UnauthorizedException;
import faang.school.postservice.model.Album;
import faang.school.postservice.repository.AlbumRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AlbumValidatorImpl implements AlbumValidator {
    private final AlbumRepository albumRepository;
    private final UserServiceClient userServiceClient;

    @Override
    public void validateUserExists(long userId) {
        try {
            userServiceClient.getUser(userId);
        } catch (FeignException.NotFound e) {
            log.error("Can not authorized user with id {}", userId);
            throw new UnauthorizedException(userId, e);
        }
    }

    @Override
    public void validateTitle(String title, long userId) {
        if (albumRepository.existsByTitleAndAuthorId(title, userId)) {
            log.error("User with ID {} already has an album titled {}.", userId, title);
            throw new DataValidationException("Album with this title already exist for this user");
        }
    }

    @Override
    public void validateAuthor(Album album, long userId) {
        if (album.getAuthorId() != userId) {
            log.error("User with ID {} is not the author of this album", userId);
            throw new ForbiddenException(userId, "author with id %d added post".formatted(userId));
        }
    }
}
