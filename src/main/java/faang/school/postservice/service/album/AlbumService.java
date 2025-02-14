package faang.school.postservice.service.album;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.dto.album.CreateAlbumRequestDto;
import faang.school.postservice.dto.album.UpdateAlbumRequestDto;
import faang.school.postservice.mapper.AlbumMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.album.filter.AlbumFilter;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final PostRepository postRepository;
    private final AlbumMapper albumMapper;
    private final UserServiceClient userServiceClient;
    private final List<AlbumFilter> albumFilters;

    @Transactional(readOnly = true)
    public AlbumDto getAlbumById(Long albumId) {
        return albumMapper.toDto(getAlbum(albumId));
    }

    @Transactional(readOnly = true)
    public List<AlbumDto> getAllAlbums(AlbumFilterDto filter) {
        List<Album> albums = albumRepository.findAll();
        return applyFilters(albums, filter);
    }

    @Transactional(readOnly = true)
    public List<AlbumDto> getUserAlbums(Long userId, AlbumFilterDto filter) {
        validateUser(userId);
        List<Album> albums = albumRepository.findByAuthorId(userId);
        return applyFilters(albums, filter);
    }

    @Transactional(readOnly = true)
    public List<AlbumDto> getUserFavoriteAlbums(Long userId, AlbumFilterDto filter) {
        validateUser(userId);
        List<Album> albums = albumRepository.findFavoriteAlbumsByUserId(userId);
        return applyFilters(albums, filter);
    }

    @Transactional
    public AlbumDto createAlbum(Long userId, CreateAlbumRequestDto request) {
        validateUser(userId);
        if (albumRepository.existsByTitleAndAuthorId(request.title(), userId)) {
            throw new IllegalArgumentException("Альбом с таким названием уже существует.");
        }
        Album album = albumMapper.toEntity(request);
        album.setAuthorId(userId);
        return albumMapper.toDto(albumRepository.save(album));
    }

    @Transactional
    public void addAlbumToFavorites(Long userId, Long albumId) {
        validateAlbum(albumId);
        albumRepository.addAlbumToFavorites(albumId, userId);
    }

    @Transactional
    public void addPostToAlbum(Long userId, Long albumId, Long postId) {
        Album album = getAndValidateUserAlbum(userId, albumId);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Пост не найден"));

        album.getPosts().add(post);
    }

    @Transactional
    public AlbumDto updateAlbum(Long userId, Long albumId, UpdateAlbumRequestDto request) {
        Album album = getAndValidateUserAlbum(userId, albumId);
        if (albumRepository.existsByTitleAndAuthorId(request.title(), userId)
                && !album.getTitle().equals(request.title())) {
            throw new IllegalArgumentException("Альбом с таким названием уже существует.");
        }

        albumMapper.updateAlbumFromDto(request, album);
        return albumMapper.toDto(album);
    }

    @Transactional
    public void deleteAlbum(Long userId, Long albumId) {
        Album album = getAndValidateUserAlbum(userId, albumId);
        albumRepository.delete(album);
    }

    @Transactional
    public void removeAlbumFromFavorites(Long userId, Long albumId) {
        validateAlbum(albumId);
        albumRepository.deleteAlbumFromFavorites(albumId, userId);
    }

    @Transactional
    public void removePostFromAlbum(Long userId, Long albumId, Long postId) {
        Album album = getAndValidateUserAlbum(userId, albumId);
        album.getPosts().removeIf(post -> post.getId().equals(postId));
    }

    private void validateAlbum(Long albumId) {
        if (!albumRepository.existsById(albumId)) {
            throw new EntityNotFoundException("Альбом не найден");
        }
    }

    private void validateUser(Long userId) {
        try {
            userServiceClient.getUser(userId);
        } catch (Exception e) {
            throw new EntityNotFoundException("Пользователь не найден");
        }
    }

    private Album getAlbum(Long albumId) {
        return albumRepository.findById(albumId)
                .orElseThrow(() -> new EntityNotFoundException("Альбом с ID " + albumId + " не найден"));
    }

    private Album getAndValidateUserAlbum(Long userId, Long albumId) {
        validateUser(userId);
        Album album = getAlbum(albumId);

        if (album.getAuthorId() != userId) {
            throw new IllegalStateException("Вы не владелец этого альбома");
        }

        return album;
    }

    private List<AlbumDto> applyFilters(List<Album> albums, AlbumFilterDto filter) {
        Stream<Album> albumStream = albums.stream();
        for (AlbumFilter albumFilter : albumFilters) {
            if (albumFilter.isApplicable(filter)) {
                albumStream = albumFilter.apply(albumStream, filter);
            }
        }

        return albumStream.map(albumMapper::toDto).collect(Collectors.toList());
    }
}