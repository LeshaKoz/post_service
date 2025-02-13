package faang.school.postservice.filters.album.impl;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.filters.album.AlbumFilter;
import faang.school.postservice.model.Album;
import org.springframework.stereotype.Component;

@Component
public class AlbumTitleFilter implements AlbumFilter {
    @Override
    public boolean isApplicable(AlbumFilterDto filters) {
        return filters.getTitlePattern() != null && !filters.getTitlePattern().isEmpty();
    }

    @Override
    public boolean filterEntity(Album album, AlbumFilterDto filters) {
        return album.getTitle().toLowerCase().contains(filters.getTitlePattern().toLowerCase());
    }
}
