package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.album.AlbumCreateDto;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.dto.album.AlbumReadDto;
import faang.school.postservice.dto.album.AlbumUpdateDto;
import faang.school.postservice.exception.BusinessException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.exception.NoAccessException;
import faang.school.postservice.filters.album.AlbumFilter;
import faang.school.postservice.mapper.AlbumMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class AlbumService {
    private final UserServiceClient userServiceClient;
    private final AlbumRepository albumRepository;
    private final AlbumMapper albumMapper;
    private final PostService postService;
    private final List<AlbumFilter> albumFilters;


    public AlbumReadDto createAlbum(long userId, AlbumCreateDto albumDto){
        validateUserId(userId);
        validateUniqueTitle(albumDto.getTitle(), userId);

        Album album = albumMapper.toEntity(albumDto);
        album.setAuthorId(userId);
        return albumMapper.toDto(albumRepository.save(album));
    }

    public AlbumReadDto getAlbumById(long albumId){
        Album album = getAlbum(albumId);
        return albumMapper.toDto(album);
    }

    public List<AlbumReadDto> getAllAlbums(AlbumFilterDto filters){
        Stream<Album> albumStream = albumRepository.findAll().stream();

        return getAlbumReadDtoWithFilters(filters, albumStream);
    }

    public List<AlbumReadDto> getUserAlbums(long userId, AlbumFilterDto filters){
        validateUserId(userId);
        Stream<Album> albumStream = albumRepository.findByAuthorId(userId).stream();

        return getAlbumReadDtoWithFilters(filters, albumStream);
    }

    public AlbumReadDto updateAlbum(long userId, long albumId, AlbumUpdateDto albumUpdateDto){
        validateUserId(userId);
        Album album = getAlbumIfOwner(albumId, userId);

        albumMapper.updateEntityFromDto(albumUpdateDto, album);
        return albumMapper.toDto(albumRepository.save(album));
    }

    public void deleteAlbum(long userId, long albumId){
        validateUserId(userId);
        Album album = getAlbumIfOwner(albumId, userId);
        albumRepository.delete(album);
    }

    public AlbumReadDto addPostToAlbum(long userId, long albumId, long postId){
        validateUserId(userId);
        Album album = getAlbumIfOwner(albumId, userId);
        Post post = postService.getPostById(postId);

        album.addPost(post);
        return albumMapper.toDto(albumRepository.save(album));
    }

    public AlbumReadDto removePostFromAlbum(long userId, long albumId, long postId){
        validateUserId(userId);
        Album album = getAlbumIfOwner(albumId, userId);
        Post post = postService.getPostById(postId);

        album.removePost(post.getId());
        return albumMapper.toDto(albumRepository.save(album));
    }

    public void addAlbumToFavorites(long userId, long albumId){
        validateUserId(userId);
        getAlbum(albumId);

        albumRepository.addAlbumToFavorites(albumId, userId);
    }

    public void removeAlbumFromFavorites(long userId, long albumId){
        albumRepository.deleteAlbumFromFavorites(albumId, userId);
    }

    public List<AlbumReadDto> getFavoriteAlbums(long userId, AlbumFilterDto filters){
        validateUserId(userId);
        Stream<Album> albumStream = albumRepository.findFavoriteAlbumsByUserId(userId).stream();
        return getAlbumReadDtoWithFilters(filters, albumStream);
    }

    private Album getAlbum(long albumId) {
        return albumRepository.findById(albumId)
                .orElseThrow(() -> new EntityNotFoundException("Альбом с id: " + albumId + " не найден"));
    }

    private List<AlbumReadDto> getAlbumReadDtoWithFilters(AlbumFilterDto filters, Stream<Album> albumStream) {
        for (AlbumFilter filter : albumFilters){
            albumStream = filter.apply(albumStream, filters);
        }

        List<Album> filteredAlbums = albumStream.toList();
        return filteredAlbums.stream()
                .map(albumMapper::toDto)
                .toList();
    }

    private void validateUserId(long userId) {
        if (userServiceClient.getUser(userId) == null) {
            throw new EntityNotFoundException("Пользователь не найден");
        }
    }

    private void validateUniqueTitle(String title, long userId) {
        if (albumRepository.existsByTitleAndAuthorId(title, userId)) {
            throw new BusinessException("Альбом с таким названием уже существует");
        }
    }

    private Album getAlbumIfOwner(long albumId, long userId) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new EntityNotFoundException("Альбом c id " + albumId + " не найден"));

        if (album.getAuthorId() != userId) {
            throw new NoAccessException("Доступ запрещен");
        }
        return album;
    }


}
