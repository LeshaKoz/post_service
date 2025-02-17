package faang.school.postservice.service.post;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.AlbumResponseDto;
import faang.school.postservice.dto.post.AlbumUsersDto;
import faang.school.postservice.enums.Visibility;
import faang.school.postservice.exception.album.AlbumAccessDeniedException;
import faang.school.postservice.model.Album;
import faang.school.postservice.repository.post.AlbumRepository;
import faang.school.postservice.strategy.album.VisibilityConverter;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static faang.school.postservice.enums.Visibility.SELECTED_USERS;
import static java.util.stream.Collectors.toMap;

@Slf4j
@Service
public class AlbumServiceImpl implements AlbumService {

    private final AlbumRepository albumRepository;
    private final UserContext userContext;
    private final Map<Visibility, VisibilityConverter> visibilities;

    public AlbumServiceImpl(AlbumRepository albumRepository, UserContext userContext, List<VisibilityConverter> converters) {
        this.albumRepository = albumRepository;
        this.userContext = userContext;
        this.visibilities = converters.stream()
                .collect(toMap(VisibilityConverter::getVisibility, Function.identity()));
    }

    @Override
    public AlbumResponseDto getAlbumById(long id) {
        Album album = albumRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(String.format("Album with id = %d not found", id)));
        log.info("Album with id = {} founded", album.getId());
        return visibilities.get(album.getVisibility()).apply(album);
    }

    @Override
    public List<AlbumResponseDto> getAlbumsByAuthorId(long authorId) {
        List<AlbumResponseDto> albums = albumRepository.findByAuthorId(authorId).stream()
                .map(album -> visibilities.get(album.getVisibility()).apply(album))
                .toList();
        log.info("Founded albums with only access for author: {}", authorId);
        return albums;
    }

    @Override
    public void changeVisibilityAlbum(long id, Visibility visibility) {
        Album album = albumRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(String.format("Album with id = %d not found", id)));
        long userId = userContext.getUserId();
        checkAuthor(userId, album);
        log.info("User with id = {} is author for album with id = {}", userId, album.getId());
        album.setVisibility(visibility);
        albumRepository.save(album);
        log.info("Visibility for album with id = {} changed on {}", album.getId(), visibility);
    }

    @Transactional
    @Override
    public void addUsersForAccessAlbum(long albumId, AlbumUsersDto albumUsersDto) {
        Album album = albumRepository.findById(albumId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Album with id = %d not found", albumId)));
        long userId = userContext.getUserId();
        checkAuthor(userId, album);
        checkVisibilityForAlbum(album, SELECTED_USERS);
        albumUsersDto.usersIds().forEach(id -> albumRepository.addUserForVisibilityAtAlbum(album.getId(), id));
    }

    private void checkAuthor(long userId, Album album) {
        if (userId != album.getAuthorId()) {
            log.error("User with id = {} isn't author for album with id = {}", userId, album.getId());
            throw new AlbumAccessDeniedException(
                    String.format("User with id = %d isn't author for album with id = %d", userId, album.getId()));
        }
    }

    private void checkVisibilityForAlbum(Album album, Visibility visibility) {
        if (!visibility.equals(album.getVisibility())) {
            log.error("Visibility isn't {} in album with id = {}", visibility, album.getId());
            throw new IllegalArgumentException(
                    String.format("Needed selected_users visibility for add users for access. Album: %d", album.getId()));
        }
    }
}
