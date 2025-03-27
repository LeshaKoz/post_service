package faang.school.postservice.service.album;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.dto.album.PostDto;

import java.util.List;
import java.util.Optional;

public interface AlbumService {

    public AlbumDto createAlbum(long userId, AlbumDto albumDto);

    public AlbumDto addPost(long albumId, long userId, long postId);

    public List<AlbumDto> showAllAlbums(Optional<AlbumFilterDto> albumFilterDto);

    public AlbumDto findById(long albumId);

    public List<AlbumDto> findByAuthorId(long authorId, Optional<AlbumFilterDto> albumFilterDto);

    public List<PostDto> findByIdWithPosts(long albumId);

    public AlbumDto addAlbumToFavorites(long albumId, long userId);

    public AlbumDto deleteAlbumFromFavorites(long albumId, long userId);

    public List<AlbumDto> findFavoriteAlbumsByUserId(long userId, Optional<AlbumFilterDto> albumFilterDto);

    public AlbumDto deleteAlbum(long albumId, long userId);

    public AlbumDto deletePost(long albumId, long userId, long postId);
}
