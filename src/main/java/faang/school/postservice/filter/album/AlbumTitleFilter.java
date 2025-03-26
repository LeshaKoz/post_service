package faang.school.postservice.filter.album;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.model.Album;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.stream.Stream;

@Component
public class AlbumTitleFilter implements AlbumFilter {
    @Override
    public boolean isApplicable(AlbumFilterDto albumFilterDto) {
        String titlePattern = albumFilterDto.getTitlePattern();
        return Objects.nonNull(titlePattern) && !titlePattern.isBlank();
    }

    @Override
    public Stream<Album> apply(Stream<Album> albums, AlbumFilterDto albumFilterDto) {
        String titlePattern = albumFilterDto.getTitlePattern().trim().toLowerCase();
        return albums.filter(album -> album.getTitle().trim().toLowerCase().contains(titlePattern));
    }
}
