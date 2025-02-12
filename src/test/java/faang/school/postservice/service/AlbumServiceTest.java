package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.album.AlbumCreateDto;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.dto.album.AlbumReadDto;
import faang.school.postservice.dto.album.AlbumUpdateDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.BusinessException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.exception.NoAccessException;
import faang.school.postservice.filters.album.AlbumFilter;
import faang.school.postservice.filters.album.impl.AlbumDateFilter;
import faang.school.postservice.filters.album.impl.AlbumFavoriteFilter;
import faang.school.postservice.filters.album.impl.AlbumTitleFilter;
import faang.school.postservice.mapper.AlbumMapperImpl;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.service.post.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AlbumServiceTest {
    private static final long ALBUM_ID = 1L;
    private static final long POST_ID = 1L;
    private static final long USER_ID = 1L;

    private UserServiceClient userServiceClient;
    private AlbumRepository albumRepository;
    private AlbumMapperImpl albumMapper;
    private PostService postService;
    private List<AlbumFilter> albumFilters;
    private AlbumService albumService;
    @Captor
    private ArgumentCaptor<Album> albumCaptor;

    private AlbumCreateDto createDto;
    private AlbumUpdateDto updateDto;
    private AlbumFilterDto filterDto;
    private UserDto userDto;
    private Album album;
    private Post post;

    @BeforeEach
    public void init() {
        album = Album.builder()
                .id(ALBUM_ID)
                .title("Title")
                .description("Description")
                .authorId(USER_ID)
                .posts(new ArrayList<>())
                .build();

        createDto = new AlbumCreateDto();
        createDto.setTitle("Test album");
        createDto.setDescription("Test description");

        updateDto = new AlbumUpdateDto();
        updateDto.setId(ALBUM_ID);
        updateDto.setTitle("Test update title");
        updateDto.setDescription("Update description");

        filterDto = new AlbumFilterDto();
        filterDto.setTitlePattern("Title");

        userDto = new UserDto(USER_ID, "Den", "email");

        userServiceClient = mock(UserServiceClient.class);
        albumRepository = mock(AlbumRepository.class);
        albumMapper = spy(AlbumMapperImpl.class);
        postService = mock(PostService.class);
        albumFilters = List.of(mock(AlbumDateFilter.class), mock(AlbumFavoriteFilter.class), mock(AlbumTitleFilter.class));
        albumService = new AlbumService(userServiceClient, albumRepository, albumMapper, postService, albumFilters);
    }

    @Test
    void createAlbumShouldThrowExceptionIfUserNotExists() {
        mockFindUserById(null);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> albumService.createAlbum(USER_ID, createDto)
        );

        assertEquals("Пользователь не найден", exception.getMessage());
    }

    @Test
    void createAlbumShouldThrowExceptionIfAlbumExist() {
        mockFindUserById(userDto);
        mockExistByOwnerTitleAndId(true);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> albumService.createAlbum(USER_ID, createDto)
        );

        assertEquals("Альбом с таким названием уже существует", exception.getMessage());
    }

    @Test
    void createAlbumShouldSaveAlbumSuccessfully() {
        mockFindUserById(userDto);
        mockExistByOwnerTitleAndId(false);

        albumService.createAlbum(USER_ID, createDto);
        verify(albumRepository, times(1)).save(albumCaptor.capture());
        Album album = albumCaptor.getValue();
        assertEquals(createDto.getTitle(), album.getTitle());
        assertEquals(createDto.getDescription(), album.getDescription());
    }

    @Test
    void getAlbumByIdShouldThrowExceptionIfAlbumNotExists() {
        mockFindAlbumById(null);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> albumService.getAlbumById(ALBUM_ID)
        );

        assertEquals("Альбом с id: " + ALBUM_ID + " не найден", exception.getMessage());
    }

    @Test
    void getAlbumByIdShouldGetSuccessfully() {
        mockFindAlbumById(album);

        AlbumReadDto resultAlbum = albumService.getAlbumById(ALBUM_ID);
        assertEquals(resultAlbum.getTitle(), album.getTitle());
    }

    @Test
    void getAllAlbumsShouldGetSuccessfully() {
        when(albumRepository.findAll()).thenReturn(List.of(album));
        mockAlbumFiltersReturnStream(Stream.of(album));

        List<AlbumReadDto> albums = albumService.getAllAlbums(filterDto);
        assertEquals(1, albums.size());
    }

    @Test
    void getUserAlbumsShouldGetSuccessfully() {
        mockFindUserById(userDto);
        when(albumRepository.findByAuthorId(USER_ID)).thenReturn(List.of(album));
        mockAlbumFiltersReturnStream(Stream.of(album));

        List<AlbumReadDto> albums = albumService.getUserAlbums(USER_ID, filterDto);
        assertEquals(1, albums.size());
    }

    @Test
    void updateAlbumShouldThrowExceptionIfAccessIsDenied() {
        album.setAuthorId(5L);
        mockFindUserById(userDto);
        mockFindAlbumById(album);

        NoAccessException exception = assertThrows(
                NoAccessException.class,
                () -> albumService.updateAlbum(USER_ID, ALBUM_ID, updateDto)
        );

        assertEquals("Доступ запрещен", exception.getMessage());
    }

    @Test
    void updateAlbumShouldUpdateSuccessfully() {
        mockFindUserById(userDto);
        mockFindAlbumById(album);

        albumService.updateAlbum(USER_ID, ALBUM_ID, updateDto);
        verify(albumRepository, times(1)).save(albumCaptor.capture());
        Album updatedAlbum = albumCaptor.getValue();

        assertEquals(updateDto.getDescription(), updatedAlbum.getDescription());
        assertEquals(updateDto.getTitle(), updatedAlbum.getTitle());
    }

    @Test
    void deleteAlbumShouldUpdateSuccessfully() {
        mockFindUserById(userDto);
        mockFindAlbumById(album);

        albumService.deleteAlbum(USER_ID, ALBUM_ID);

        verify(albumRepository, times(1)).delete(album);
    }

    @Test
    void addPostToAlbumShouldAddSuccessfully() {
        post = Post.builder()
                .id(POST_ID)
                .build();


        mockFindUserById(userDto);
        mockFindAlbumById(album);
        when(postService.getPostById(POST_ID)).thenReturn(post);

        albumService.addPostToAlbum(USER_ID, ALBUM_ID, POST_ID);

        verify(albumRepository, times(1)).save(albumCaptor.capture());
        Album capturedAlbum = albumCaptor.getValue();
        assertTrue(capturedAlbum.getPosts().contains(post));
    }

    @Test
    void removePostFromAlbumShouldRemoveSuccessfully() {
        post = Post.builder()
                .id(POST_ID)
                .build();
        album.setPosts(new ArrayList<>(List.of(post)));

        mockFindUserById(userDto);
        mockFindAlbumById(album);
        when(postService.getPostById(POST_ID)).thenReturn(post);

        albumService.removePostFromAlbum(USER_ID, ALBUM_ID, POST_ID);

        verify(albumRepository, times(1)).save(albumCaptor.capture());
        Album capturedAlbum = albumCaptor.getValue();
        assertFalse(capturedAlbum.getPosts().contains(post));
    }

    @Test
    void addAlbumFromFavoritesShouldSuccess() {
        mockFindUserById(userDto);
        mockFindAlbumById(album);
        albumService.addAlbumToFavorites(USER_ID, ALBUM_ID);

        verify(albumRepository, times(1)).addAlbumToFavorites(ALBUM_ID, USER_ID);
    }

    @Test
    void removeAlbumFromFavoritesShouldSuccess() {
        albumService.removeAlbumFromFavorites(USER_ID, ALBUM_ID);

        verify(albumRepository, times(1)).deleteAlbumFromFavorites(ALBUM_ID, USER_ID);
    }

    @Test
    void getFavoriteAlbums_Success() {
        mockFindUserById(userDto);
        when(albumRepository.findFavoriteAlbumsByUserId(USER_ID)).thenReturn(List.of(album));
        mockAlbumFiltersReturnStream(Stream.of(album));

        List<AlbumReadDto> result = albumService.getFavoriteAlbums(USER_ID, filterDto);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Title", result.get(0).getTitle());
    }


    private void mockFindAlbumById(Album album) {
        when(albumRepository.findById(ALBUM_ID)).thenReturn(Optional.ofNullable(album));
    }

    private void mockExistByOwnerTitleAndId(boolean t) {
        when(albumRepository.existsByTitleAndAuthorId(createDto.getTitle(), USER_ID)).thenReturn(t);
    }

    private void mockFindUserById(UserDto userDto) {
        when(userServiceClient.getUser(USER_ID)).thenReturn(userDto);
    }

    private void mockAlbumFiltersReturnStream(Stream<Album> stream) {
        albumFilters.forEach(albumFilter -> {
            lenient().when(albumFilter.isApplicable(any())).thenReturn(true);
            lenient().when(albumFilter.apply(any(), any())).thenReturn(stream);
        });
    }
}
