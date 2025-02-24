package faang.school.postservice.filter.album;

import faang.school.postservice.filter.Filter;
import faang.school.postservice.model.Album;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AlbumTitleFilter implements Filter<Album, AlbumFilterDto> {
    @Override
    public boolean isApplicable(AlbumFilterDto dto) {
        return dto.title() != null;
    }

    @Override
    public List<Album> apply(List<Album> albums, AlbumFilterDto filters) {
        return albums.stream()
                .filter(album -> filters.title().equals(album.getTitle()))
                .toList();
    }
}
