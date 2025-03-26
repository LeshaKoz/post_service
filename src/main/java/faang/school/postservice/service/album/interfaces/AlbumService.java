package faang.school.postservice.service.album.interfaces;

import faang.school.postservice.dto.album.AlbumCreateUpdateDto;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.AlbumFilterDto;

import java.util.List;

public interface AlbumService {

    AlbumDto createAlbum(AlbumCreateUpdateDto createDto);

    AlbumDto addPostToAlbum(long albumId, long postId);

    void deletePostFromAlbum(long albumId, long postId);

    void addAlbumToFavorites(long albumId);

    void deleteAlbumFromFavorites(long albumId);

    AlbumDto getAlbumById(long albumId);

    List<AlbumDto> getAllAlbums(AlbumFilterDto filterDto);

    List<AlbumDto> getUserAlbums(long albumAuthorUserId, AlbumFilterDto filterDto);

    List<AlbumDto> getUserFavoriteAlbums(long albumAuthorUserId, AlbumFilterDto filterDto);

    AlbumDto updateAlbum(long albumId, AlbumCreateUpdateDto updateDto);

    void deleteAlbum(long albumId);

}
