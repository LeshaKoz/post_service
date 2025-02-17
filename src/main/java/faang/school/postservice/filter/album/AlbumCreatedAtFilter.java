package faang.school.postservice.filter.album;

import faang.school.postservice.filter.Filter;
import faang.school.postservice.model.Album;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class AlbumCreatedAtFilter implements Filter<Album, AlbumFilterDto> {
    @Override
    public boolean isApplicable(AlbumFilterDto dto) {
        return dto.createdAt() != null;
    }

    @Override
    public List<Album> apply(List<Album> albums, AlbumFilterDto filters) {
        return albums.stream()
                .filter(f -> compareDates(filters.createdAt(), f.getCreatedAt()))
                .toList();
    }

    private boolean compareDates(LocalDateTime createdAtFilter, LocalDateTime createdAt) {
        return createdAtFilter.getYear() == createdAt.getYear()
                && createdAtFilter.getMonth() == createdAt.getMonth()
                && createdAtFilter.getDayOfMonth() == createdAt.getDayOfMonth();
    }
}
