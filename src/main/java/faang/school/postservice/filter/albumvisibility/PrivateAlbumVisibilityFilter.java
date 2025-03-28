package faang.school.postservice.filter.albumvisibility;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.album.AlbumResponseDto;
import faang.school.postservice.exception.AlbumAccessDeniedException;
import faang.school.postservice.mapper.AlbumMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.AlbumVisibility;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static faang.school.postservice.model.AlbumVisibility.PRIVATE;

@Component
@RequiredArgsConstructor
public class PrivateAlbumVisibilityFilter implements AlbumVisibilityFilter {

    private final AlbumMapper albumMapper;
    private final UserContext userContext;

    @Override
    public AlbumResponseDto apply(Album album) {
        long userId = userContext.getUserId();
        if (userId != album.getAuthorId()) {
            throw new AlbumAccessDeniedException(
                    String.format("Access denied for user with id = %d for album with id = %d. " +
                                    "User with id = %d isn't author for album with id = %d",
                            userId, album.getId(), userId, album.getId()));
        }
        return albumMapper.toDto(album);
    }

    @Override
    public AlbumVisibility getAlbumVisibility() {
        return PRIVATE;
    }
}
