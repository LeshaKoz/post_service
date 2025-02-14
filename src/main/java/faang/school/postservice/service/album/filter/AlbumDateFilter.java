package faang.school.postservice.service.album.filter;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.model.Album;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class AlbumDateFilter implements AlbumFilter {

    @Override
    public boolean isApplicable(AlbumFilterDto filter) {
        return filter.fromDate() != null || filter.toDate() != null;
    }

    @Override
    public Stream<Album> apply(Stream<Album> albumStream, AlbumFilterDto filter) {
        return albumStream.filter(album ->
                ((filter.fromDate() == null) || album.getCreatedAt().isAfter(filter.fromDate()))
                        && ((filter.toDate() == null) || album.getCreatedAt().isBefore(filter.toDate()))
        );
    }
}
