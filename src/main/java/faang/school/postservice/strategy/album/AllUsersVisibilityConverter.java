package faang.school.postservice.strategy.album;

import faang.school.postservice.dto.post.AlbumResponseDto;
import faang.school.postservice.enums.Visibility;
import faang.school.postservice.mapper.post.AlbumMapper;
import faang.school.postservice.model.Album;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static faang.school.postservice.enums.Visibility.ALL_USERS;

@Component
@RequiredArgsConstructor
public class AllUsersVisibilityConverter implements VisibilityConverter {

    private final AlbumMapper albumMapper;

    @Override
    public AlbumResponseDto apply(Album album) {
        return albumMapper.toDto(album);
    }

    @Override
    public Visibility getVisibility() {
        return ALL_USERS;
    }
}
