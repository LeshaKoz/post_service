package faang.school.postservice.service.album;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.album.*;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.AlbumMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.album.filter.AlbumFilter;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AlbumServiceTest {

    @Mock
    private AlbumRepository albumRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @Spy
    private AlbumMapper albumMapper = Mappers.getMapper(AlbumMapper.class);

    private List<AlbumFilter> albumFilters = new ArrayList<>();

    private AlbumService albumService;

    private UserDto dummyUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        albumService = new AlbumService(albumRepository, postRepository, albumMapper, userServiceClient, albumFilters);
        dummyUser = new UserDto(1L, "username", "email@example.com");
    }

    @Test
    void getAlbumById_ReturnsDtoWhenFound() {
        Long albumId = 1L;
        Album album = createAlbum(1L, 123L, LocalDateTime.now());

        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));

        AlbumDto result = albumService.getAlbumById(albumId);

        assertNotNull(result);
        assertEquals(album.getId(), result.id());
        verify(albumMapper).toDto(album);
    }

    @Test
    void getAllAlbums_ReturnsListDto() {
        AlbumFilterDto filter = new AlbumFilterDto(null, null, null, null);
        Album album = createAlbum(1L, 123L, LocalDateTime.now());

        when(albumRepository.findAll()).thenReturn(List.of(album));

        List<AlbumDto> result = albumService.getAllAlbums(filter);

        assertEquals(1, result.size());
        assertEquals(album.getId(), result.get(0).id());
    }

    @Test
    void getUserAlbums_ReturnsListDto() {
        Long userId = 1L;
        AlbumFilterDto filter = new AlbumFilterDto(null, null, null, null);
        Album album = createAlbum(1L, 123L, LocalDateTime.now());

        when(albumRepository.findByAuthorId(userId)).thenReturn(List.of(album));
        when(userServiceClient.getUser(userId)).thenReturn(dummyUser);

        List<AlbumDto> result = albumService.getUserAlbums(userId, filter);
        assertEquals(1, result.size());
        assertEquals(album.getId(), result.get(0).id());
    }

    @Test
    void getUserFavoriteAlbums_ReturnsListDto() {
        Long userId = 1L;
        AlbumFilterDto filter = new AlbumFilterDto(null, null, null, null);
        Album album = createAlbum(1L, 123L, LocalDateTime.now());;

        when(albumRepository.findFavoriteAlbumsByUserId(userId)).thenReturn(List.of(album));
        when(userServiceClient.getUser(userId)).thenReturn(dummyUser);

        List<AlbumDto> result = albumService.getUserFavoriteAlbums(userId, filter);
        assertEquals(1, result.size());
        assertEquals(album.getId(), result.get(0).id());
    }

    @Test
    void createAlbum_Success() {
        Long userId = 1L;
        CreateAlbumRequestDto request = new CreateAlbumRequestDto("New Album", "New album description");
        when(albumRepository.existsByTitleAndAuthorId("New Album", userId)).thenReturn(false);
        when(userServiceClient.getUser(userId)).thenReturn(dummyUser);

        when(albumRepository.save(ArgumentMatchers.any(Album.class))).thenAnswer(invocation -> {
            Album album = invocation.getArgument(0);
            album.setId(1L);
            return album;
        });

        AlbumDto result = albumService.createAlbum(userId, request);
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("New Album", result.title());
    }

    @Test
    void createAlbum_ThrowsExceptionIfTitleExists() {
        Long userId = 1L;
        CreateAlbumRequestDto request = new CreateAlbumRequestDto(
                "Existing Album",
                "Existing description");
        when(albumRepository.existsByTitleAndAuthorId("Existing Album", userId)).thenReturn(true);
        when(userServiceClient.getUser(userId)).thenReturn(dummyUser);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> albumService.createAlbum(userId, request));
        assertEquals("Альбом с таким названием уже существует.", ex.getMessage());
    }

    @Test
    void addAlbumToFavorites_CallsRepositoryMethod() {
        Long userId = 1L;
        Long albumId = 1L;
        when(albumRepository.existsById(albumId)).thenReturn(true);

        albumService.addAlbumToFavorites(userId, albumId);
        verify(albumRepository).addAlbumToFavorites(albumId, userId);
    }

    @Test
    void addAlbumToFavorites_ThrowsIfAlbumNotFound() {
        Long userId = 1L;
        Long albumId = 1L;
        when(albumRepository.existsById(albumId)).thenReturn(false);

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> albumService.addAlbumToFavorites(userId, albumId));
        assertEquals("Альбом не найден", ex.getMessage());
    }

    @Test
    void addPostToAlbum_Success() {
        Long userId = 1L;
        Long albumId = 1L;
        Long postId = 1L;

        Album album = createAlbum(1L, 1L, LocalDateTime.now());

        Post post = new Post();
        post.setId(postId);

        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));
        when(userServiceClient.getUser(userId)).thenReturn(dummyUser);
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        albumService.addPostToAlbum(userId, albumId, postId);
        assertTrue(album.getPosts().contains(post));
    }

    @Test
    void addPostToAlbum_ThrowsIfPostNotFound() {
        Long userId = 1L;
        Long albumId = 1L;
        Long postId = 1L;

        Album album = createAlbum(1L, 1L, LocalDateTime.now());

        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));
        when(userServiceClient.getUser(userId)).thenReturn(dummyUser);
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> albumService.addPostToAlbum(userId, albumId, postId));
        assertEquals("Пост не найден", ex.getMessage());
    }

    @Test
    void updateAlbum_Success() {
        Long userId = 1L;
        Long albumId = 1L;
        UpdateAlbumRequestDto request = new UpdateAlbumRequestDto("Updated Title", "Updated description");

        Album album = createAlbum(1L, 1L, LocalDateTime.now());

        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));
        when(userServiceClient.getUser(userId)).thenReturn(dummyUser);
        when(albumRepository.existsByTitleAndAuthorId("Updated Title", userId)).thenReturn(false);

        AlbumDto updated = albumService.updateAlbum(userId, albumId, request);
        assertEquals("Updated Title", updated.title());
    }

    @Test
    void updateAlbum_ThrowsIfDuplicateTitle() {
        Long userId = 1L;
        Long albumId = 1L;
        UpdateAlbumRequestDto request = new UpdateAlbumRequestDto(
                "Duplicate Title",
                "Duplicate description");

        Album album = createAlbum(1L, 1L, LocalDateTime.now());

        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));
        when(userServiceClient.getUser(userId)).thenReturn(dummyUser);
        when(albumRepository.existsByTitleAndAuthorId("Duplicate Title", userId)).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> albumService.updateAlbum(userId, albumId, request));
        assertEquals("Альбом с таким названием уже существует.", ex.getMessage());
    }

    @Test
    void deleteAlbum_Success() {
        Long userId = 1L;
        Long albumId = 1L;

        Album album = createAlbum(1L, 1L, LocalDateTime.now());

        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));
        when(userServiceClient.getUser(userId)).thenReturn(dummyUser);

        albumService.deleteAlbum(userId, albumId);
        verify(albumRepository).delete(album);
    }

    @Test
    void removeAlbumFromFavorites_Success() {
        Long userId = 1L;
        Long albumId = 1L;
        when(albumRepository.existsById(albumId)).thenReturn(true);

        albumService.removeAlbumFromFavorites(userId, albumId);
        verify(albumRepository).deleteAlbumFromFavorites(albumId, userId);
    }

    @Test
    void removeAlbumFromFavorites_ThrowsIfAlbumNotFound() {
        Long userId = 1L;
        Long albumId = 1L;
        when(albumRepository.existsById(albumId)).thenReturn(false);

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> albumService.removeAlbumFromFavorites(userId, albumId));
        assertEquals("Альбом не найден", ex.getMessage());
    }

    @Test
    void removePostFromAlbum_Success() {
        Long userId = 1L;
        Long albumId = 1L;
        Long postId = 1L;

        Album album = new Album();
        album.setId(albumId);
        album.setAuthorId(userId);
        album.setCreatedAt(LocalDateTime.now());
        Post post = new Post();
        post.setId(postId);
        album.setPosts(new ArrayList<>(List.of(post)));

        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));
        when(userServiceClient.getUser(userId)).thenReturn(dummyUser);

        albumService.removePostFromAlbum(userId, albumId, postId);
        assertTrue(album.getPosts().isEmpty());
    }

    @Test
    void operations_ThrowIfUserIsNotOwner() {
        Long userId = 1L;
        Long albumId = 1L;

        Album album = new Album();
        album.setId(albumId);

        album.setAuthorId(2L);
        album.setCreatedAt(LocalDateTime.now());
        album.setPosts(new ArrayList<>());

        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));
        when(userServiceClient.getUser(userId)).thenReturn(dummyUser);

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> albumService.deleteAlbum(userId, albumId));
        assertEquals("Вы не владелец этого альбома", ex.getMessage());
    }

    @Test
    void validateUser_ThrowsIfUserNotFound() {
        Long userId = 1L;
        when(userServiceClient.getUser(userId)).thenThrow(new RuntimeException());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> albumService.getUserAlbums(userId, new AlbumFilterDto(null, null,
                        null, null)));
        assertEquals("Пользователь не найден", ex.getMessage());
    }

    private Album createAlbum(Long id, Long authorId, LocalDateTime createdAt) {
        return Album.builder()
                .id(id)
                .authorId(authorId)
                .title("Album " + id)
                .description("Описание альбома " + id)
                .createdAt(createdAt)
                .posts(new ArrayList<>())
                .build();
    }
}
