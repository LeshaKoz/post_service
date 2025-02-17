package faang.school.postservice.service.post;

import faang.school.postservice.dto.post.AlbumResponseDto;
import faang.school.postservice.dto.post.AlbumUsersDto;
import faang.school.postservice.enums.Visibility;

import java.util.List;

public interface AlbumService {

    AlbumResponseDto getAlbumById(long id);

    List<AlbumResponseDto> getAlbumsByAuthorId(long authorId);

    void changeVisibilityAlbum(long id, Visibility visibility);

    void addUsersForAccessAlbum(long id, AlbumUsersDto albumUsersDto);
}
