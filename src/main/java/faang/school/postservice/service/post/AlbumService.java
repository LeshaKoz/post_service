package faang.school.postservice.service.post;

import faang.school.postservice.dto.post.AlbumRequestDto;
import faang.school.postservice.dto.post.AlbumResponseDto;
import faang.school.postservice.dto.post.AlbumUsersDto;
import faang.school.postservice.enums.Visibility;
import faang.school.postservice.filter.album.AlbumFilterDto;

import java.util.List;

public interface AlbumService {

    AlbumResponseDto createAlbum(AlbumRequestDto dto);

    AlbumResponseDto addPostToAlbum(long postId, long albumId);

    void deletePostFromAlbum(long postId, long albumId);

    void addAlbumToFavorites(long albumId);

    void deleteAlbumFromFavorites(long albumId);

    List<AlbumResponseDto> getAllOwnAlbums(AlbumFilterDto filters);

    List<AlbumResponseDto> getAllAlbumsByFilters(int pageNumber, int pageSize, AlbumFilterDto filters);

    List<AlbumResponseDto> getAllFavoritesOwnAlbums(AlbumFilterDto filters);

    AlbumResponseDto updateAlbum(long id, AlbumRequestDto dto);

    void deleteAlbumById(long id);

    AlbumResponseDto getAlbumById(long id);

    List<AlbumResponseDto> getAlbumsByAuthorId(long authorId);

    void changeVisibilityAlbum(long id, Visibility visibility);

    void addUsersForAccessAlbum(long id, AlbumUsersDto albumUsersDto);
}
