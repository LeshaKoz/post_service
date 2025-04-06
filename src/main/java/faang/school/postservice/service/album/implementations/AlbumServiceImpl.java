package faang.school.postservice.service.album.implementations;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.album.AlbumCreateUpdateDto;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.filter.album.AlbumFilter;
import faang.school.postservice.mapper.album.AlbumMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.service.album.interfaces.AlbumService;
import faang.school.postservice.service.post.interfaces.PostService;
import faang.school.postservice.validator.album.AlbumValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class AlbumServiceImpl implements AlbumService {
    private final UserContext userContext;
    private final AlbumValidator albumValidator;
    private final AlbumRepository albumRepository;
    private final AlbumMapper albumMapper;
    private final PostService postService;
    private final List<AlbumFilter> albumFilters;

    @Override
    @Transactional
    public AlbumDto createAlbum(AlbumCreateUpdateDto createUpdateDto) {
        long userId = userContext.getUserId();
        albumValidator.validateUserExists(userId);
        albumValidator.validateTitle(createUpdateDto.getTitle(), userId);
        Album albumToSave = albumMapper.toEntity(createUpdateDto);
        albumToSave.setAuthorId(userId);
        Album albumSaved = albumRepository.save(albumToSave);
        return albumMapper.toDtoList(albumSaved);
    }

    @Override
    @Transactional
    public AlbumDto addPostToAlbum(long albumId, long postId) {
        long userId = userContext.getUserId();
        Album album = getAlbum(albumId);
        albumValidator.validateAuthor(album, userId);
        Post post = postService.getPostById(postId);
        album.addPost(post);
        Album savedAlbum = albumRepository.save(album);
        return albumMapper.toDtoList(savedAlbum);
    }

    @Override
    @Transactional
    public void deletePostFromAlbum(long albumId, long postId) {
        long userId = userContext.getUserId();
        Album album = getAlbum(albumId);
        albumValidator.validateAuthor(album, userId);
        if (album.getPosts().stream().anyMatch(post -> post.getId() == postId)) {
            album.removePost(postId);
            albumRepository.save(album);
        }
    }

    @Override
    @Transactional
    public void addAlbumToFavorites(long albumId) {
        long userId = userContext.getUserId();
        Album album = getAlbum(albumId);
        albumValidator.validateAuthor(album, userId);
        albumRepository.addAlbumToFavorites(albumId, userId);
    }

    @Override
    @Transactional
    public void deleteAlbumFromFavorites(long albumId) {
        long userId = userContext.getUserId();
        Album album = getAlbum(albumId);
        albumValidator.validateAuthor(album, userId);
        albumRepository.deleteAlbumFromFavorites(albumId, userId);
    }

    @Override
    public AlbumDto getAlbumById(long albumId) {
        Album album = getAlbum(albumId);
        return albumMapper.toDtoList(album);
    }

    @Override
    @Transactional
    public List<AlbumDto> getAllAlbums(AlbumFilterDto filterDto) {
        Stream<Album> albums = StreamSupport.stream(albumRepository.findAll().spliterator(), false);
        List<Album> filteredAlbums = filterAlbums(albums, filterDto);
        return albumMapper.toDtoList(filteredAlbums);
    }

    @Override
    @Transactional
    public List<AlbumDto> getUserAlbums(long authorId, AlbumFilterDto filterDto) {
        Stream<Album> albums = albumRepository.findByAuthorId(authorId);
        List<Album> filteredAlbums = filterAlbums(albums, filterDto);
        return albumMapper.toDtoList(filteredAlbums);
    }

    @Override
    @Transactional
    public List<AlbumDto> getUserFavoriteAlbums(long authorId, AlbumFilterDto filterDto) {
        Stream<Album> albums = albumRepository.findFavoriteAlbumsByUserId(authorId);
        List<Album> filteredAlbums = filterAlbums(albums, filterDto);
        return albumMapper.toDtoList(filteredAlbums);
    }

    @Override
    @Transactional
    public AlbumDto updateAlbum(long albumId, AlbumCreateUpdateDto createUpdateDto) {
        long userId = userContext.getUserId();
        Album album = getAlbum(albumId);
        albumValidator.validateAuthor(album, userId);
        albumValidator.validateTitle(createUpdateDto.getTitle(), userId);
        albumMapper.update(createUpdateDto, album);
        album = albumRepository.save(album);
        return albumMapper.toDtoList(album);
    }

    @Override
    @Transactional
    public void deleteAlbum(long albumId) {
        long userId = userContext.getUserId();
        Album album = getAlbum(albumId);
        albumValidator.validateAuthor(album, userId);
        albumRepository.delete(album);
    }

    private Album getAlbum(long albumId) {
        return albumRepository.findById(albumId)
                .orElseThrow(() -> new EntityNotFoundException(albumId));
    }

    private List<Album> filterAlbums(Stream<Album> albums, AlbumFilterDto albumFilterDto) {
        return albumFilters.stream()
                .filter(filter -> filter.isApplicable(albumFilterDto))
                .reduce(
                        albums,
                        ((albumStream, albumFilter) -> albumFilter.apply(albumStream, albumFilterDto)),
                        (s1, s2) -> s2
                ).toList();
    }
}
