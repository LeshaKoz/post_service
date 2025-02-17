package faang.school.postservice.strategy.album;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.AlbumResponseDto;
import faang.school.postservice.enums.Visibility;
import faang.school.postservice.exception.album.AlbumAccessDeniedException;
import faang.school.postservice.mapper.post.AlbumMapper;
import faang.school.postservice.model.Album;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static faang.school.postservice.enums.Visibility.ONLY_AUTHOR;

@Component
@RequiredArgsConstructor
public class OnlyAuthorVisibilityConverter implements VisibilityConverter {

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
    public Visibility getVisibility() {
        return ONLY_AUTHOR;
    }
}
