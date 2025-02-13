package faang.school.postservice.filters.album.impl;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.filters.album.AlbumFilter;
import faang.school.postservice.model.Album;
import faang.school.postservice.repository.AlbumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AlbumFavoriteFilter implements AlbumFilter {
    private final AlbumRepository albumRepository;
    private final UserContext userContext;

    @Override
    public boolean isApplicable(AlbumFilterDto filters) {
        return filters.getIsFavoritePattern() != null;
    }

    @Override
    public boolean filterEntity(Album album, AlbumFilterDto filters) {
        long userId = userContext.getUserId();
        return filters.getIsFavoritePattern().equals(albumRepository.isFavorite(album.getId(), userId));
    }
}
