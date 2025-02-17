package faang.school.postservice.strategy.album;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.AlbumResponseDto;
import faang.school.postservice.enums.Visibility;
import faang.school.postservice.exception.album.AlbumAccessDeniedException;
import faang.school.postservice.mapper.post.AlbumMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.repository.post.AlbumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import static faang.school.postservice.enums.Visibility.SELECTED_USERS;

@Component
@RequiredArgsConstructor
public class SelectedUsersVisibilityConverter implements VisibilityConverter {

    private final AlbumRepository albumRepository;
    private final AlbumMapper albumMapper;
    private final UserContext userContext;

    @Override
    public AlbumResponseDto apply(Album album) {
        long userId = userContext.getUserId();
        List<Long> selectedUsersForAlbum = albumRepository.findSelectedUsersForAlbum(album.getId());
        if (selectedUsersForAlbum.contains(userId)) {
            return albumMapper.toDto(album);
        }
        throw new AlbumAccessDeniedException(
                String.format("Access denied for user with id = %d for album with id = %d", userId, album.getId()));
    }

    @Override
    public Visibility getVisibility() {
        return SELECTED_USERS;
    }
}
