package faang.school.postservice.filter.album;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.model.Album;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class DateFilter implements AlbumFilter {
    @Override
    public boolean isApplicable(AlbumFilterDto filter) {
        return filter.getFromDate() != null;
    }

    @Override
    public Stream<Album> apply(Stream<Album> elements, AlbumFilterDto filter) {
        return elements.filter(album -> album.getCreatedAt().toLocalDate().equals(filter.getFromDate()));
    }
}
