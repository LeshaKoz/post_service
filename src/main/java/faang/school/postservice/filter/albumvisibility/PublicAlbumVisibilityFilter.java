package faang.school.postservice.filter.albumvisibility;

import faang.school.postservice.dto.album.AlbumResponseDto;
import faang.school.postservice.mapper.AlbumMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.AlbumVisibility;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static faang.school.postservice.model.AlbumVisibility.PUBLIC;

@Component
@RequiredArgsConstructor
public class PublicAlbumVisibilityFilter implements AlbumVisibilityFilter {

    private final AlbumMapper albumMapper;

    @Override
    public AlbumResponseDto apply(Album album) {
        return albumMapper.toDto(album);
    }

    @Override
    public AlbumVisibility getAlbumVisibility() {
        return PUBLIC;
    }
}
