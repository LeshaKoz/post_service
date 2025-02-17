package faang.school.postservice.strategy.album;

import faang.school.postservice.dto.post.AlbumResponseDto;
import faang.school.postservice.enums.Visibility;
import faang.school.postservice.model.Album;

public interface VisibilityConverter {

    AlbumResponseDto apply(Album album);

    Visibility getVisibility();
}
