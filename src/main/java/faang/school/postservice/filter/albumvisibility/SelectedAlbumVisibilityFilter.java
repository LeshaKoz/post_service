package faang.school.postservice.filter.albumvisibility;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.album.AlbumResponseDto;
import faang.school.postservice.exception.AlbumAccessDeniedException;
import faang.school.postservice.mapper.AlbumMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.AlbumVisibility;
import faang.school.postservice.repository.AlbumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import static faang.school.postservice.model.AlbumVisibility.SELECTED;

@Component
@RequiredArgsConstructor
public class SelectedAlbumVisibilityFilter implements AlbumVisibilityFilter {

    private final AlbumMapper albumMapper;
    private final UserContext userContext;
    private final AlbumRepository albumRepository;

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
    public AlbumVisibility getAlbumVisibility() {
        return SELECTED;
    }
}
