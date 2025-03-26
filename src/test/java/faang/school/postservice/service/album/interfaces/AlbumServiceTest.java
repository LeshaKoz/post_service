package faang.school.postservice.service.album.interfaces;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.album.AlbumCreateUpdateDto;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.exception.ForbiddenException;
import faang.school.postservice.exception.UnauthorizedException;
import faang.school.postservice.filter.album.AlbumDescriptionFilter;
import faang.school.postservice.filter.album.AlbumFilter;
import faang.school.postservice.filter.album.AlbumTitleFilter;
import faang.school.postservice.mapper.album.AlbumMapperImpl;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.service.album.implementations.AlbumServiceImpl;
import faang.school.postservice.service.post.interfaces.PostService;
import faang.school.postservice.validator.album.AlbumValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlbumServiceTest {
    @Mock
    private UserContext userContext;
    @Mock
    AlbumValidator albumValidator;
    @Mock
    private AlbumRepository albumRepository;
    @Spy
    private AlbumMapperImpl albumMapper;
    @Mock
    private PostService postService;
    private AlbumService albumService;
    private List<AlbumFilter> albumFilters;

    @BeforeEach
    void setUp() {
        albumFilters = List.of(
                new AlbumTitleFilter(),
                new AlbumDescriptionFilter());
        albumService = new AlbumServiceImpl(userContext, albumValidator, albumRepository,
                albumMapper, postService, albumFilters);
    }

    @Test
    void testCreateAlbum_whenUserNotExists() {
        long userId = 1L;
        AlbumCreateUpdateDto createUpdateDto = new AlbumCreateUpdateDto();
        createUpdateDto.setTitle("Title");
        createUpdateDto.setDescription("Description");
        Exception e = new Exception();
        when(userContext.getUserId()).thenReturn(userId);
        doThrow(new UnauthorizedException(userId, e)).when(albumValidator).validateUserExists(userId);

        assertThrows(UnauthorizedException.class, () -> albumService.createAlbum(createUpdateDto));

        verify(userContext, times(1)).getUserId();
        verify(albumValidator, times(1)).validateUserExists(userId);
    }

    @Test
    void testCreateAlbum_whenTitleIsNotUnique() {
        long userId = 1L;
        String title = "Title";
        String description = "Description";
        AlbumCreateUpdateDto createUpdateDto = new AlbumCreateUpdateDto();
        createUpdateDto.setTitle(title);
        createUpdateDto.setDescription(description);
        when(userContext.getUserId()).thenReturn(userId);
        doThrow(new DataValidationException("Album with this title already exist for this user"))
                .when(albumValidator).validateTitle(title, userId);

        assertThrows(DataValidationException.class, () -> albumService.createAlbum(createUpdateDto));

        verify(userContext, times(1)).getUserId();
        verify(albumValidator, times(1)).validateTitle(title, userId);
    }

    @Test
    void testCreateAlbumSuccessfully() {
        long userId = 1L;
        String title = "Title";
        String description = "Description";
        AlbumCreateUpdateDto createUpdateDto = new AlbumCreateUpdateDto();
        createUpdateDto.setTitle(title);
        createUpdateDto.setDescription(description);
        Album album = albumMapper.toEntity(createUpdateDto);
        album.setAuthorId(userId);
        when(userContext.getUserId()).thenReturn(userId);
        when(albumRepository.save(album)).thenReturn(album);

        AlbumDto albumDto = assertDoesNotThrow(() -> albumService.createAlbum(createUpdateDto));
        assertEquals(title, albumDto.getTitle());
        assertEquals(description, albumDto.getDescription());

        verify(userContext, times(1)).getUserId();
        verify(albumRepository, times(1)).save(album);
        verify(albumValidator, times(1)).validateTitle(title, userId);
    }

    @Test
    void testAddPost_whenAlbumIsNotExists() {
        long albumId = 1L;
        long postId = 2L;
        when(albumRepository.findById(albumId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> albumService.addPostToAlbum(albumId, postId));

        verify(albumRepository, times(1)).findById(albumId);
    }

    @Test
    void testAddPost_whenUserIsNotAuthor() {
        long albumId = 1L;
        long postId = 2L;
        long userId = 3L;
        long authorId = 4L;
        Album album = new Album();
        album.setAuthorId(authorId);
        when(userContext.getUserId()).thenReturn(userId);
        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));
        doThrow(new ForbiddenException(userId)).when(albumValidator).validateAuthor(album, userId);

        assertThrows(ForbiddenException.class, () -> albumService.addPostToAlbum(albumId, postId));

        verify(userContext, times(1)).getUserId();
        verify(albumRepository, times(1)).findById(albumId);
        verify(albumValidator, times(1)).validateAuthor(album, userId);
    }

    @Test
    void testAddPostSuccessfully() {
        long albumId = 1L;
        long postId = 2L;
        long authorId = 3L;
        List<Long> expectedPostIds = List.of(postId);
        Album album = new Album();
        album.setAuthorId(authorId);
        album.setId(albumId);
        album.setPosts(new ArrayList<>());
        Post post = new Post();
        post.setId(postId);
        when(userContext.getUserId()).thenReturn(authorId);
        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));
        when(postService.getPost(postId)).thenReturn(post);
        when(albumRepository.save(album)).thenReturn(album);

        AlbumDto albumDto = assertDoesNotThrow(() -> albumService.addPostToAlbum(albumId, postId));
        assertEquals(albumId, albumDto.getId());
        assertEquals(expectedPostIds, albumDto.getPostIds());

        verify(userContext, times(1)).getUserId();
        verify(albumRepository, times(1)).findById(albumId);
        verify(postService, times(1)).getPost(postId);
        verify(albumRepository, times(1)).save(album);
    }

    @Test
    void testDeletePostFromAlbum_whenPostIsNotExists() {
        long albumId = 1L;
        long postId = 2L;
        long authorId = 3L;
        Album album = new Album();
        album.setAuthorId(authorId);
        album.setPosts(new ArrayList<>());
        when(userContext.getUserId()).thenReturn(authorId);
        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));

        assertDoesNotThrow(() -> albumService.deletePostFromAlbum(albumId, postId));

        verify(userContext, times(1)).getUserId();
        verify(albumRepository, times(1)).findById(albumId);
        verify(albumRepository, times(0)).save(album);
    }

    @Test
    void testDeletePostFromAlbum_whenPostExists() {
        long albumId = 1L;
        long postId = 2L;
        long authorId = 3L;
        Post post = new Post();
        post.setId(postId);
        Album album = new Album();
        album.setAuthorId(authorId);
        album.setPosts(new ArrayList<>(List.of(post)));
        when(userContext.getUserId()).thenReturn(authorId);
        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));
        when(albumRepository.save(album)).thenReturn(album);

        assertDoesNotThrow(() -> albumService.deletePostFromAlbum(albumId, postId));
        assertEquals(0, album.getPosts().size());

        verify(userContext, times(1)).getUserId();
        verify(albumRepository, times(1)).findById(albumId);
        verify(albumRepository, times(1)).save(album);
    }

    @Test
    void testAddAlbumToFavorites() {
        long albumId = 1L;
        long authorId = 2L;
        Album album = new Album();
        album.setId(albumId);
        when(userContext.getUserId()).thenReturn(authorId);
        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));

        assertDoesNotThrow(() -> albumService.addAlbumToFavorites(albumId));

        verify(userContext, times(1)).getUserId();
        verify(albumRepository, times(1)).findById(albumId);
        verify(albumValidator, times(1)).validateAuthor(album, authorId);
        verify(albumRepository, times(1)).addAlbumToFavorites(albumId, authorId);
    }

    @Test
    void testDeleteAlbumFromFavorites() {
        long albumId = 1L;
        long authorId = 2L;
        Album album = new Album();
        album.setId(albumId);
        when(userContext.getUserId()).thenReturn(authorId);
        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));

        assertDoesNotThrow(() -> albumService.deleteAlbumFromFavorites(albumId));

        verify(userContext, times(1)).getUserId();
        verify(albumRepository, times(1)).findById(albumId);
        verify(albumValidator, times(1)).validateAuthor(album, authorId);
        verify(albumRepository, times(1)).deleteAlbumFromFavorites(albumId, authorId);
    }

    @Test
    void testGetAlbumById() {
        long albumId = 1L;
        String title = "Title";
        String description = "Description";
        Album album = new Album();
        album.setId(albumId);
        album.setTitle(title);
        album.setDescription(description);
        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));

        AlbumDto albumDto = albumService.getAlbumById(albumId);
        assertEquals(albumId, albumDto.getId());
        assertEquals(title, albumDto.getTitle());
        assertEquals(description, albumDto.getDescription());

        verify(albumRepository, times(1)).findById(albumId);
    }

    @Test
    void testGetAllAlbums() {
        String titlePattern = "activities";
        String descriptionPattern = "best";
        Stream<Album> albums = Stream.of(
                initAlbum("Concert activities", "Best concerts poster"),
                initAlbum("Moonshine brewing", "The best recipes for homemade alcohol"),
                initAlbum("Educational tasks activities",
                        "Homework development best tasks activities"),
                initAlbum("Work assignments", "Service technical assignment plans"),
                initAlbum("Volunteering assignments", "Assistance to animal shelters"),
                initAlbum("Exhibition activities", "Best technical innovations")
        );
        AlbumFilterDto albumFilterDto = AlbumFilterDto.builder()
                .titlePattern(titlePattern)
                .descriptionPattern(descriptionPattern)
                .build();
        when(albumRepository.findAll()).thenReturn(albums::iterator);
        List<AlbumDto> filteredAlbums = assertDoesNotThrow(() -> albumService.getAllAlbums(albumFilterDto));

        assertEquals(3, filteredAlbums.size());
        assertTrue(filteredAlbums.stream().allMatch(album -> album.getTitle().trim().toLowerCase()
                .contains(titlePattern.trim().toLowerCase())));
        assertTrue(filteredAlbums.stream().allMatch(album -> album.getDescription().trim().toLowerCase()
                .contains(descriptionPattern.trim().toLowerCase())));

        verify(albumRepository, times(1)).findAll();
    }

    @Test
    void testGetUserAlbums() {
        filterUserAlbums(albumService::getUserAlbums,
                (repository, albums) -> when(repository.findByAuthorId(anyLong())).thenReturn(albums));
        verify(albumRepository, times(1)).findByAuthorId(anyLong());
    }

    @Test
    void testGetUserFavoriteAlbums() {
        filterUserAlbums(albumService::getUserFavoriteAlbums,
                (repository, albums) -> when(repository.findFavoriteAlbumsByUserId(anyLong())).thenReturn(albums));
        verify(albumRepository, times(1)).findFavoriteAlbumsByUserId(anyLong());
    }

    @Test
    void testUpdateAlbum() {
        long albumId = 1L;
        long authorId = 2L;
        String newTitle = "New title";
        String newDescription = "New description";
        Album album = new Album();
        album.setId(albumId);
        album.setTitle("Previous title");
        album.setDescription("Previous description");
        album.setAuthorId(authorId);
        AlbumCreateUpdateDto createUpdateDto = AlbumCreateUpdateDto.builder()
                .title(newTitle)
                .description(newDescription)
                .build();
        when(userContext.getUserId()).thenReturn(authorId);
        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));
        when(albumRepository.save(album)).thenReturn(album);

        AlbumDto albumDto = assertDoesNotThrow(() -> albumService.updateAlbum(albumId, createUpdateDto));
        assertEquals(albumId, albumDto.getId());
        assertEquals(authorId, albumDto.getAuthorId());
        assertEquals(newTitle, albumDto.getTitle());
        assertEquals(newDescription, albumDto.getDescription());

        verify(userContext, times(1)).getUserId();
        verify(albumRepository, times(1)).findById(albumId);
        verify(albumValidator, times(1)).validateAuthor(album, authorId);
        verify(albumValidator, times(1)).validateTitle(createUpdateDto.getTitle(), authorId);
        verify(albumRepository, times(1)).save(album);
    }

    @Test
    void testDeleteAlbum() {
        long albumId = 1L;
        long authorId = 2L;
        Album album = new Album();
        album.setId(albumId);
        album.setAuthorId(authorId);
        when(userContext.getUserId()).thenReturn(authorId);
        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));

        assertDoesNotThrow(() -> albumService.deleteAlbum(albumId));

        verify(userContext, times(1)).getUserId();
        verify(albumRepository, times(1)).findById(albumId);
        verify(albumValidator, times(1)).validateAuthor(album, authorId);
    }

    private Album initAlbum(String title, String description) {
        Album album = new Album();
        album.setTitle(title);
        album.setDescription(description);
        return album;
    }

    private void filterUserAlbums(BiFunction<Long, AlbumFilterDto, List<AlbumDto>> method,
                                  BiConsumer<AlbumRepository, Stream<Album>> repository) {
        long userId = 1L;
        String titlePattern = "activities";
        String descriptionPattern = "best";
        Stream<Album> albums = Stream.of(
                initAlbum("Concert activities", "Best concerts poster"),
                initAlbum("Moonshine brewing", "The best recipes for homemade alcohol"),
                initAlbum("Educational tasks activities",
                        "Homework development best tasks activities"),
                initAlbum("Work assignments", "Service technical assignment plans"),
                initAlbum("Volunteering assignments", "Assistance to animal shelters"),
                initAlbum("Exhibition activities", "Best technical innovations")
        );
        AlbumFilterDto albumFilterDto = AlbumFilterDto.builder()
                .titlePattern(titlePattern)
                .descriptionPattern(descriptionPattern)
                .build();
        repository.accept(albumRepository, albums);
        List<AlbumDto> filteredAlbums = assertDoesNotThrow(() -> method.apply(userId, albumFilterDto));

        assertEquals(3, filteredAlbums.size());
        assertTrue(filteredAlbums.stream()
                .allMatch(album -> album.getTitle().trim().toLowerCase().contains(titlePattern.trim().toLowerCase())));
        assertTrue(filteredAlbums.stream()
                .allMatch(album -> album.getDescription().trim().toLowerCase()
                        .contains(descriptionPattern.trim().toLowerCase())));
    }
}
