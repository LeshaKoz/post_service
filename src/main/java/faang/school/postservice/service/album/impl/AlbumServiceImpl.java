package faang.school.postservice.service.album.impl;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.dto.album.PostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.album.AlbumMapper;
import faang.school.postservice.mapper.album.PostMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.album.AlbumRepository;
import faang.school.postservice.service.album.AlbumService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
public class AlbumServiceImpl implements AlbumService {

    private final AlbumRepository albumRepository;
    private final UserServiceClient userServiceClient;
    private final AlbumMapper albumMapper;
    private final PostMapper postMapper;
    private final PostRepository postRepository;

    @Override
    public AlbumDto createAlbum(long userId, AlbumDto albumDto) {
        checkIfUserExist(userId);
        if (!isNull(albumRepository.findAlbumByAuthorId((userId)))) {
            throw new RuntimeException("Album already exists");
        }
        Album album = albumMapper.toEntity(albumDto);
        album.setAuthorId(userId);
        Album savedAlbum = albumRepository.save(album);
        return albumMapper.toDto(savedAlbum);
    }

    @Override
    public AlbumDto addPost(long albumId, long userId, long postId) {
        Album album = albumRepository.findAlbumById(albumId);
        Optional<Post> post = postRepository.findById(postId);
        checkIfAlbumExist(albumId);
        checkIfUserExist(userId);
        checkIfPostExist(postId);
        checkIfUserHaveAccess(userId, albumId);
        if (album.getPosts().contains(post.get())) {
            throw new EntityExistsException("Post already exists in this album");
        }
        album.addPost(post.get());
        albumRepository.save(album);
        return albumMapper.toDto(album);
    }

    @Override
    public List<AlbumDto> showAllAlbums(Optional<AlbumFilterDto> albumFilterDto) {
        List<Album> allAlbums = albumRepository.findAll();
        Predicate<Album> albumPredicate = Album -> true;
        if (albumFilterDto.isPresent()) {
            albumPredicate = makePredicateFilter(albumPredicate, albumFilterDto.get());
        }
        return allAlbums.stream().filter(albumPredicate).map(albumMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public AlbumDto findById(long albumId) {
        Album album = albumRepository.findAlbumById(albumId);
        checkIfAlbumExist(albumId);
        return albumMapper.toDto(album);
    }

    @Override
    public List<AlbumDto> findByAuthorId(long authorId, Optional<AlbumFilterDto> albumFilterDto) {
        List<Album> albums = albumRepository.findAlbumByAuthorId(authorId);
        Predicate<Album> albumPredicate = Album -> true;
        checkIfUserExist(authorId);
        if (albumFilterDto.isPresent()) {
            albumPredicate = makePredicateFilter(albumPredicate, albumFilterDto.get());
        }
        return albums.stream().filter(albumPredicate).map(albumMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<PostDto> findByIdWithPosts(long albumId) {
        Album album = albumRepository.findAlbumById(albumId);
        checkIfAlbumExist(albumId);
        return album.getPosts().stream().map(postMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public AlbumDto addAlbumToFavorites(long albumId, long userId) {
        checkIfAlbumExist(albumId);
        checkIfUserExist(userId);
        if (!isNull(albumRepository.findAlbumInFavorites(albumId))) {
            throw new EntityExistsException("This album is already in favorites");
        }
        albumRepository.addAlbumToFavorite(albumId, userId);
        return albumMapper.toDto(albumRepository.findAlbumById(albumId));
    }

    @Override
    public AlbumDto deleteAlbumFromFavorites(long albumId, long userId) {
        checkIfAlbumExist(albumId);
        checkIfUserExist(userId);
        albumRepository.deleteAlbumFromFavorite(albumId, userId);
        return albumMapper.toDto(albumRepository.findAlbumById(albumId));
    }

    @Override
    public List<AlbumDto> findFavoriteAlbumsByUserId(long userId, Optional<AlbumFilterDto> albumFilterDto) {
        checkIfUserExist(userId);
        List<Album> favoriteAlbums = new ArrayList<>();
        Predicate<Album> albumPredicate = Album -> true;
        long[] favoriteAlbumIds = albumRepository.findFavoriteAlbumIdsByUserId(userId);
        for (long favoriteAlbumId : favoriteAlbumIds) {
            favoriteAlbums.add(albumRepository.findAlbumById(favoriteAlbumId));
        }
        albumPredicate = makePredicateFilter(albumPredicate, albumFilterDto.get());
        return favoriteAlbums.stream().filter(albumPredicate).map(albumMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public AlbumDto deleteAlbum(long albumId, long userId) {
        checkIfAlbumExist(albumId);
        checkIfUserExist(userId);
        checkIfUserHaveAccess(userId, albumId);
        albumRepository.deleteAlbumById(albumId);
        return albumMapper.toDto(albumRepository.findAlbumById(albumId));
    }

    @Override
    public AlbumDto deletePost(long albumId, long userId, long postId) {
        Album album = albumRepository.findAlbumById(albumId);
        checkIfAlbumExist(albumId);
        checkIfUserExist(userId);
        checkIfPostExist(postId);
        checkIfUserHaveAccess(userId, albumId);
        if (album.getPosts().contains(postRepository.findById(postId).get())) {
            throw new DataValidationException("Post is not in this album");
        }
        album.removePost(postId);
        return albumMapper.toDto(album);
    }

    private Predicate<Album> makePredicateFilter(Predicate<Album> albumPredicate, AlbumFilterDto albumFilterDto) {
        String titleFilter = albumFilterDto.getTitle();
        LocalDate dateOfCreation = albumFilterDto.getDateOfCreation();
        if (!isNull(titleFilter)) {
            albumPredicate = albumPredicate.and(Album -> Album.getTitle().equals(titleFilter));
        }
        if (!isNull(dateOfCreation)) {
            albumPredicate = albumPredicate.and(Album -> Album.getCreatedAt().getYear() == (dateOfCreation.getYear()));
        }
        return albumPredicate;
    }

    private void checkIfAlbumExist(long albumId) {
        if (isNull(albumRepository.findAlbumById(albumId))) {
            throw new EntityNotFoundException("Album not found");
        }
    }

    private void checkIfUserExist(long userId) {
        if (isNull(userServiceClient.getUser(userId))) {
            throw new EntityNotFoundException("User not found");
        }
    }

    private void checkIfUserHaveAccess(long userId, long albumId) {
        if (albumRepository.findAlbumById(albumId).getAuthorId() != userId) {
            throw new DataValidationException("You don't have access to edit this album");
        }
    }

    private void checkIfPostExist(long postId) {
        if (postRepository.findById(postId).isEmpty()) {
            throw new EntityNotFoundException("Post not found");
        }
    }
}
