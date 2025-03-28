package faang.school.postservice.filter.albumvisibility;

import faang.school.postservice.dto.album.AlbumResponseDto;
import faang.school.postservice.model.AlbumVisibility;
import faang.school.postservice.model.Album;

public interface AlbumVisibilityFilter {

    AlbumResponseDto apply(Album album);

    AlbumVisibility getAlbumVisibility();
}
