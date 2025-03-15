package faang.school.postservice.service.album;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.album.AlbumCreateDto;
import faang.school.postservice.dto.album.AlbumEditDto;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.dto.album.AlbumReadDto;
import faang.school.postservice.exception.BusinessException;
import faang.school.postservice.filter.album.AlbumFilter;
import faang.school.postservice.filter.album.TitleFilter;
import faang.school.postservice.mapper.album.AlbumMapperImpl;
import faang.school.postservice.model.Album;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AlbumServiceTest {
    private static final Long ALBUM_ID = 1L;
    private static final Long OTHER_ALBUM_ID = 2L;
    private static final Long AUTHOR_ID = 1L;
    private static final Long OTHER_AUTHOR_ID = 2L;
    private static final String ALBUM_TITLE = "Memories";
    private static final String OTHER_ALBUM_TITLE = "Peace";
    private static final String ALBUM_DESCRIPTION = "It's all my memories";
    private static final String OTHER_ALBUM_DESCRIPTION = "You can finally relax";

    @Mock
    private UserService userService;

    @Spy
    private AlbumMapperImpl albumMapper;

    @Mock
    private AlbumRepository albumRepository;

    @Mock
    private UserContext userContext;

    @Mock
    private TitleFilter titleFilter;

    private AlbumService albumService;

    private List<AlbumFilter> filters;

    @BeforeEach
    void setUp() {
        filters = List.of(titleFilter);
        albumService = new AlbumService(
                userService,
                albumMapper,
                albumRepository,
                userContext,
                filters
        );
    }

    @Test
    void testAlbumCreate() {
        AlbumCreateDto albumCreateDto = AlbumCreateDto.builder()
                .title(ALBUM_TITLE)
                .description(ALBUM_DESCRIPTION)
                .authorId(AUTHOR_ID)
                .build();

        Album album = Album.builder()
                .title(ALBUM_TITLE)
                .description(ALBUM_DESCRIPTION)
                .authorId(AUTHOR_ID)
                .build();

        Mockito.when(userService.isUserExists(AUTHOR_ID)).thenReturn(true);
        Mockito.when(albumRepository.existsByTitleAndAuthorId(ALBUM_TITLE, AUTHOR_ID)).thenReturn(false);
        Mockito.when(albumMapper.toEntity(albumCreateDto)).thenReturn(album);

        AlbumReadDto expectedResult = AlbumReadDto.builder()
                .title(ALBUM_TITLE)
                .description(ALBUM_DESCRIPTION)
                .authorId(AUTHOR_ID)
                .build();

        Mockito.when(albumMapper.toReadDto(album)).thenReturn(expectedResult);
        Mockito.when(albumRepository.save(album)).thenReturn(album);

        AlbumReadDto result = albumService.createAlbum(albumCreateDto);
        Mockito.verify(userService).isUserExists(AUTHOR_ID);
        Mockito.verify(albumRepository).existsByTitleAndAuthorId(ALBUM_TITLE, AUTHOR_ID);
        Mockito.verify(albumMapper).toEntity(albumCreateDto);
        assertEquals(result, expectedResult);
    }

    @Test
    void testAlbumCreateIfUserDoesNotExist() {
        AlbumCreateDto albumCreateDto = AlbumCreateDto.builder()
                .title(ALBUM_TITLE)
                .description(ALBUM_DESCRIPTION)
                .authorId(AUTHOR_ID)
                .build();

        Mockito.when(userService.isUserExists(AUTHOR_ID)).thenReturn(false);

        assertThrows(BusinessException.class, () -> albumService.createAlbum(albumCreateDto));
    }

    @Test
    void testAlbumCreateIfUserHasAlbumWithSameTitle() {
        AlbumCreateDto albumCreateDto = AlbumCreateDto.builder()
                .title(ALBUM_TITLE)
                .description(ALBUM_DESCRIPTION)
                .authorId(AUTHOR_ID)
                .build();

        Mockito.when(albumRepository.existsByTitleAndAuthorId(ALBUM_TITLE, AUTHOR_ID)).thenReturn(true);

        assertThrows(BusinessException.class, () -> albumService.createAlbum(albumCreateDto));
    }

    @Test
    void testAddAlbumToFavorites() {
        Album album = Album.builder()
                .id(ALBUM_ID)
                .title(ALBUM_TITLE)
                .description(ALBUM_DESCRIPTION)
                .authorId(AUTHOR_ID)
                .build();

        AlbumReadDto expectedResult = AlbumReadDto.builder()
                .id(ALBUM_ID)
                .title(ALBUM_TITLE)
                .description(ALBUM_DESCRIPTION)
                .authorId(AUTHOR_ID)
                .build();

        Mockito.when(albumRepository.findById(ALBUM_ID)).thenReturn(Optional.of(album));
        Mockito.when(userContext.getUserId()).thenReturn(AUTHOR_ID);
        Mockito.doNothing().when(albumRepository).addAlbumToFavorites(ALBUM_ID, AUTHOR_ID);

        AlbumReadDto result = albumService.addAlbumToFavorites(ALBUM_ID);
        Mockito.verify(albumRepository).addAlbumToFavorites(ALBUM_ID, AUTHOR_ID);
        assertEquals(expectedResult, result);
    }

    @Test
    void testDeleteAlbumFromFavorites() {
        Album album = Album.builder()
                .id(ALBUM_ID)
                .title(ALBUM_TITLE)
                .description(ALBUM_DESCRIPTION)
                .authorId(AUTHOR_ID)
                .build();

        AlbumReadDto expectedResult = AlbumReadDto.builder()
                .id(ALBUM_ID)
                .title(ALBUM_TITLE)
                .description(ALBUM_DESCRIPTION)
                .authorId(AUTHOR_ID)
                .build();

        Mockito.when(albumRepository.findById(ALBUM_ID)).thenReturn(Optional.of(album));
        Mockito.when(userContext.getUserId()).thenReturn(AUTHOR_ID);
        Mockito.doNothing().when(albumRepository).deleteAlbumFromFavorites(ALBUM_ID, AUTHOR_ID);

        AlbumReadDto result = albumService.deleteAlbumFromFavorites(ALBUM_ID);
        Mockito.verify(albumRepository).deleteAlbumFromFavorites(ALBUM_ID, AUTHOR_ID);
        assertEquals(expectedResult, result);
    }

    @Test
    void testFindAlbumById() {
        Album album = Album.builder()
                .id(ALBUM_ID)
                .title(ALBUM_TITLE)
                .description(ALBUM_DESCRIPTION)
                .authorId(AUTHOR_ID)
                .build();

        AlbumReadDto expectedResult = AlbumReadDto.builder()
                .id(ALBUM_ID)
                .title(ALBUM_TITLE)
                .description(ALBUM_DESCRIPTION)
                .authorId(AUTHOR_ID)
                .build();

        Mockito.when(albumRepository.findById(ALBUM_ID)).thenReturn(Optional.of(album));
        AlbumReadDto result = albumService.findAlbumById(ALBUM_ID);
        Mockito.verify(albumRepository).findById(ALBUM_ID);
        assertEquals(expectedResult, result);
    }

    @Test
    void testFindAuthorAlbumsByFilters() {
        AlbumFilterDto albumFilterDto = AlbumFilterDto.builder()
                .titlePattern(ALBUM_TITLE)
                .build();

        Album album = Album.builder()
                .id(ALBUM_ID)
                .title(ALBUM_TITLE)
                .authorId(AUTHOR_ID)
                .build();

        List<AlbumReadDto> expectedResult = List.of(AlbumReadDto.builder()
                .id(ALBUM_ID)
                .title(ALBUM_TITLE)
                .authorId(AUTHOR_ID)
                .build());

        List<Album> albums = List.of(album);
        Stream<Album> albumStream = albums.stream();

        Mockito.when(albumRepository.findAllByAuthorId(AUTHOR_ID)).thenReturn(albums);
        Mockito.when(titleFilter.isApplicable(any())).thenReturn(true);
        Mockito.when(titleFilter.apply(any(), any())).thenReturn(albumStream);

        List<AlbumReadDto> result = albumService.findAuthorAlbumsByFilters(albumFilterDto, AUTHOR_ID);
        Mockito.verify(albumRepository).findAllByAuthorId(AUTHOR_ID);
        Mockito.verify(titleFilter).isApplicable(any());
        Mockito.verify(titleFilter).apply(any(), any());
        assertEquals(expectedResult, result);
    }

    @Test
    void testFindAuthorAlbumsByFiltersIfFiltersAreEmpty() {
        AlbumFilterDto albumFilterDto = AlbumFilterDto.builder().build();

        Album album = Album.builder()
                .id(ALBUM_ID)
                .authorId(AUTHOR_ID)
                .build();

        List<AlbumReadDto> expectedResult = List.of(AlbumReadDto.builder()
                .id(ALBUM_ID)
                .authorId(AUTHOR_ID)
                .build());
        List<Album> albums = List.of(album);

        Mockito.when(albumRepository.findAllByAuthorId(AUTHOR_ID)).thenReturn(albums);
        Mockito.when(titleFilter.isApplicable(any())).thenReturn(false);

        List<AlbumReadDto> result = albumService.findAuthorAlbumsByFilters(albumFilterDto, AUTHOR_ID);
        Mockito.verify(albumRepository).findAllByAuthorId(AUTHOR_ID);
        Mockito.verify(titleFilter).isApplicable(any());
        assertEquals(expectedResult, result);
    }

    @Test
    void testFindAuthorAlbumsByFiltersIfAuthorHasNotAnyAlbum() {
        AlbumFilterDto albumFilterDto = AlbumFilterDto.builder()
                .titlePattern(ALBUM_TITLE)
                .build();

        List<Album> albums = List.of();

        Mockito.when(albumRepository.findAllByAuthorId(AUTHOR_ID)).thenReturn(albums);
        Mockito.when(titleFilter.isApplicable(any())).thenReturn(true);
        Mockito.when(titleFilter.apply(any(), any())).thenReturn(albums.stream());

        List<AlbumReadDto> result = albumService.findAuthorAlbumsByFilters(albumFilterDto, AUTHOR_ID);
        Mockito.verify(albumRepository).findAllByAuthorId(AUTHOR_ID);
        Mockito.verify(titleFilter).isApplicable(any());
        Mockito.verify(titleFilter).apply(any(), any());
        assertTrue(result.isEmpty());
    }

    @Test
    void testFindAllAlbumsByFilters() {
        AlbumFilterDto albumFilterDto = AlbumFilterDto.builder()
                .titlePattern(ALBUM_TITLE)
                .build();

        Album album = Album.builder()
                .id(ALBUM_ID)
                .title(ALBUM_TITLE)
                .authorId(AUTHOR_ID)
                .build();

        List<AlbumReadDto> expectedResult = List.of(AlbumReadDto.builder()
                .id(ALBUM_ID)
                .title(ALBUM_TITLE)
                .authorId(AUTHOR_ID)
                .build());

        List<Album> albums = List.of(album);
        Stream<Album> albumStream = albums.stream();

        Mockito.when(albumRepository.findAll()).thenReturn(albums);
        Mockito.when(titleFilter.isApplicable(any())).thenReturn(true);
        Mockito.when(titleFilter.apply(any(), any())).thenReturn(albumStream);

        List<AlbumReadDto> result = albumService.findAllAlbumsByFilters(albumFilterDto);
        Mockito.verify(albumRepository).findAll();
        Mockito.verify(titleFilter).isApplicable(any());
        Mockito.verify(titleFilter).apply(any(), any());
        assertEquals(expectedResult, result);
    }

    @Test
    void testFindAllAlbumsByFiltersIfFiltersAreEmpty() {
        AlbumFilterDto albumFilterDto = AlbumFilterDto.builder().build();

        Album album = Album.builder()
                .id(ALBUM_ID)
                .authorId(AUTHOR_ID)
                .build();

        List<AlbumReadDto> expectedResult = List.of(AlbumReadDto.builder()
                .id(ALBUM_ID)
                .authorId(AUTHOR_ID)
                .build());
        List<Album> albums = List.of(album);

        Mockito.when(albumRepository.findAll()).thenReturn(albums);
        Mockito.when(titleFilter.isApplicable(any())).thenReturn(false);

        List<AlbumReadDto> result = albumService.findAllAlbumsByFilters(albumFilterDto);
        Mockito.verify(albumRepository).findAll();
        Mockito.verify(titleFilter).isApplicable(any());
        assertEquals(expectedResult, result);
    }

    @Test
    void testFindAllAlbumsByFiltersIfAnyAlbumDoesNotExist() {
        AlbumFilterDto albumFilterDto = AlbumFilterDto.builder()
                .titlePattern(ALBUM_TITLE)
                .build();

        List<Album> albums = List.of();

        Mockito.when(albumRepository.findAll()).thenReturn(albums);
        Mockito.when(titleFilter.isApplicable(any())).thenReturn(true);
        Mockito.when(titleFilter.apply(any(), any())).thenReturn(albums.stream());

        List<AlbumReadDto> result = albumService.findAllAlbumsByFilters(albumFilterDto);
        Mockito.verify(albumRepository).findAll();
        Mockito.verify(titleFilter).isApplicable(any());
        Mockito.verify(titleFilter).apply(any(), any());
        assertTrue(result.isEmpty());
    }

    @Test
    void testFindFavoriteAlbumsByFilters() {
        AlbumFilterDto albumFilterDto = AlbumFilterDto.builder()
                .titlePattern(ALBUM_TITLE)
                .build();

        Album album = Album.builder()
                .id(ALBUM_ID)
                .title(ALBUM_TITLE)
                .authorId(AUTHOR_ID)
                .build();

        List<AlbumReadDto> expectedResult = List.of(AlbumReadDto.builder()
                .id(ALBUM_ID)
                .title(ALBUM_TITLE)
                .authorId(AUTHOR_ID)
                .build());

        List<Album> albums = List.of(album);
        Stream<Album> albumStream = albums.stream();

        Mockito.when(userContext.getUserId()).thenReturn(AUTHOR_ID);
        Mockito.when(albumRepository.findFavoriteAlbumsByUserId(AUTHOR_ID)).thenReturn(albums);
        Mockito.when(titleFilter.isApplicable(any())).thenReturn(true);
        Mockito.when(titleFilter.apply(any(), any())).thenReturn(albumStream);

        List<AlbumReadDto> result = albumService.findFavoriteAlbumsByFilters(albumFilterDto);
        Mockito.verify(userContext).getUserId();
        Mockito.verify(albumRepository).findFavoriteAlbumsByUserId(AUTHOR_ID);
        Mockito.verify(titleFilter).isApplicable(any());
        Mockito.verify(titleFilter).apply(any(), any());
        assertEquals(expectedResult, result);
    }

    @Test
    void testFindFavoriteAlbumsByFiltersIfFiltersAreEmpty() {
        AlbumFilterDto albumFilterDto = AlbumFilterDto.builder().build();

        Album album = Album.builder()
                .id(ALBUM_ID)
                .authorId(AUTHOR_ID)
                .build();

        List<AlbumReadDto> expectedResult = List.of(AlbumReadDto.builder()
                .id(ALBUM_ID)
                .authorId(AUTHOR_ID)
                .build());
        List<Album> albums = List.of(album);

        Mockito.when(userContext.getUserId()).thenReturn(AUTHOR_ID);
        Mockito.when(albumRepository.findFavoriteAlbumsByUserId(AUTHOR_ID)).thenReturn(albums);
        Mockito.when(titleFilter.isApplicable(any())).thenReturn(false);

        List<AlbumReadDto> result = albumService.findFavoriteAlbumsByFilters(albumFilterDto);
        Mockito.verify(userContext).getUserId();
        Mockito.verify(albumRepository).findFavoriteAlbumsByUserId(AUTHOR_ID);
        Mockito.verify(titleFilter).isApplicable(any());
        assertEquals(expectedResult, result);
    }

    @Test
    void testFindFavoriteAlbumsByFiltersIfAuthorHasNotAnyAlbum() {
        AlbumFilterDto albumFilterDto = AlbumFilterDto.builder()
                .titlePattern(ALBUM_TITLE)
                .build();

        List<Album> albums = List.of();
        Stream<Album> albumStream = albums.stream();

        Mockito.when(userContext.getUserId()).thenReturn(AUTHOR_ID);
        Mockito.when(albumRepository.findFavoriteAlbumsByUserId(AUTHOR_ID)).thenReturn(albums);
        Mockito.when(titleFilter.isApplicable(any())).thenReturn(true);
        Mockito.when(titleFilter.apply(any(), any())).thenReturn(albumStream);

        List<AlbumReadDto> result = albumService.findFavoriteAlbumsByFilters(albumFilterDto);
        Mockito.verify(userContext).getUserId();
        Mockito.verify(albumRepository).findFavoriteAlbumsByUserId(AUTHOR_ID);
        Mockito.verify(titleFilter).isApplicable(any());
        Mockito.verify(titleFilter).apply(any(), any());
        assertTrue(result.isEmpty());
    }

    @Test
    void testEditAlbum() {
        Album album = Album.builder()
                .id(ALBUM_ID)
                .title(ALBUM_TITLE)
                .description(ALBUM_DESCRIPTION)
                .authorId(AUTHOR_ID)
                .build();

        AlbumEditDto albumEditDto = AlbumEditDto.builder()
                .id(ALBUM_ID)
                .title(OTHER_ALBUM_TITLE)
                .description(OTHER_ALBUM_DESCRIPTION)
                .authorId(AUTHOR_ID)
                .build();

        AlbumReadDto expectedResult = AlbumReadDto.builder()
                .id(ALBUM_ID)
                .title(OTHER_ALBUM_TITLE)
                .description(OTHER_ALBUM_DESCRIPTION)
                .authorId(AUTHOR_ID)
                .build();

        Mockito.when(userContext.getUserId()).thenReturn(AUTHOR_ID);
        Mockito.when(albumRepository.findById(ALBUM_ID)).thenReturn(Optional.of(album));
        Mockito.doNothing().when(albumMapper).update(album, albumEditDto);
        Mockito.when(albumMapper.toReadDto(album)).thenReturn(expectedResult);
        Mockito.when(albumRepository.save(album)).thenReturn(album);

        AlbumReadDto result = albumService.editAlbum(albumEditDto);
        Mockito.verify(userContext).getUserId();
        Mockito.verify(albumRepository).findById(ALBUM_ID);
        Mockito.verify(albumMapper).update(album, albumEditDto);
        Mockito.verify(albumMapper).toReadDto(album);
        Mockito.verify(albumRepository).save(album);
        assertEquals(expectedResult, result);
    }

    @Test
    void testEditAlbumIfUserIdsAreDifferent() {
        Album album = Album.builder()
                .id(ALBUM_ID)
                .title(ALBUM_TITLE)
                .description(ALBUM_DESCRIPTION)
                .authorId(AUTHOR_ID)
                .build();

        AlbumEditDto albumEditDto = AlbumEditDto.builder()
                .id(ALBUM_ID)
                .title(OTHER_ALBUM_TITLE)
                .description(OTHER_ALBUM_DESCRIPTION)
                .authorId(OTHER_AUTHOR_ID)
                .build();

        Mockito.when(userContext.getUserId()).thenReturn(AUTHOR_ID);
        Mockito.when(albumRepository.findById(ALBUM_ID)).thenReturn(Optional.of(album));

        assertThrows(BusinessException.class, () -> albumService.editAlbum(albumEditDto));
    }

    @Test
    void testEditAlbumIfAlbumDoesNotExist() {
        AlbumEditDto albumEditDto = AlbumEditDto.builder()
                .id(ALBUM_ID)
                .title(ALBUM_TITLE)
                .description(ALBUM_DESCRIPTION)
                .authorId(AUTHOR_ID)
                .build();

        Mockito.when(userContext.getUserId()).thenReturn(AUTHOR_ID);
        Mockito.when(albumRepository.findById(ALBUM_ID)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> albumService.editAlbum(albumEditDto));
    }

    @Test
    void testDeleteAlbum() {
        Mockito.when(albumRepository.existsById(ALBUM_ID)).thenReturn(true).thenReturn(false);
        Mockito.doNothing().when(albumRepository).deleteById(ALBUM_ID);

        albumService.deleteAlbum(ALBUM_ID);
        Mockito.verify(albumRepository).existsById(ALBUM_ID);
        Mockito.verify(albumRepository).deleteById(ALBUM_ID);
        assertFalse(albumRepository.existsById(ALBUM_ID));
    }

    @Test
    void testDeleteAlbumIfAlbumDoesNotExist() {
        Mockito.when(albumRepository.existsById(ALBUM_ID)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> albumService.deleteAlbum(ALBUM_ID));
    }
}