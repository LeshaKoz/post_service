package faang.school.postservice.filter.album;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.model.Album;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.stream.Stream;

@Component
public class DateTitle implements AlbumFilter {
    @Override
    public boolean isApplicable(AlbumFilterDto filter) {
        return filter.getFromDate() != null && filter.getFromDate().isBefore(LocalDateTime.now());
    }

    @Override
    public Stream<Album> apply(Stream<Album> elements, AlbumFilterDto filter) {
        return elements.filter(album -> album.getCreatedAt().equals(filter.getFromDate()));
    }
}
