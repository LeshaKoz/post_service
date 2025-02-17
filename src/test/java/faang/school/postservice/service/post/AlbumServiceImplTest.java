package faang.school.postservice.service.post;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.AlbumResponseDto;
import faang.school.postservice.dto.post.AlbumUsersDto;
import faang.school.postservice.enums.Visibility;
import faang.school.postservice.exception.album.AlbumAccessDeniedException;
import faang.school.postservice.filter.Filter;
import faang.school.postservice.filter.album.AlbumFilterDto;
import faang.school.postservice.filter.album.AlbumTitleFilter;
import faang.school.postservice.mapper.post.AlbumMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.repository.post.AlbumRepository;
import faang.school.postservice.repository.post.PostRepository;
import faang.school.postservice.strategy.album.VisibilityConverter;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlbumServiceImplTest {

    @Mock
    private AlbumRepository albumRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserContext userContext;

    @Mock
    private VisibilityConverter allUsersConverter;

    @Mock
    private VisibilityConverter followersConverter;

    @Mock
    private UserServiceClient userServiceClient;

    @Spy
    private AlbumMapper albumMapper;

    private final List<Filter<Album, AlbumFilterDto>> filters = new ArrayList<>();

    private AlbumServiceImpl albumService;

    @BeforeEach
    public void init() {
        filters.add(new AlbumTitleFilter());
        when(allUsersConverter.getVisibility()).thenReturn(Visibility.ALL_USERS);
        when(followersConverter.getVisibility()).thenReturn(Visibility.FOLLOWERS);
        albumService = new AlbumServiceImpl(albumRepository, postRepository, albumMapper, userContext,
                List.of(allUsersConverter, followersConverter), userServiceClient, filters);
    }

    @Test
    public void testGetAlbumById() {
        long albumId = 1L;
        when(albumRepository.findById(albumId)).thenReturn(Optional.of(getAlbum(albumId, 1L, Visibility.ALL_USERS)));
        when(allUsersConverter.apply(getAlbum(albumId, 1L, Visibility.ALL_USERS))).thenReturn(getAlbumResponseDto(albumId));

        AlbumResponseDto result = albumService.getAlbumById(albumId);

        assertNotNull(result);
        assertEquals(albumId, result.id());
        verify(albumRepository).findById(albumId);
        verify(allUsersConverter).apply(getAlbum(albumId, 1L, Visibility.ALL_USERS));
    }

    @Test
    public void testGetAlbumByIdWhenAlbumNotFound() {
        long albumId = 1L;
        when(albumRepository.findById(albumId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> albumService.getAlbumById(albumId));
        verify(albumRepository).findById(albumId);
    }

    @Test
    public void testGetAlbumsByAuthorId() {
        long authorId = 1L;

        when(albumRepository.findByAuthorId(authorId)).thenReturn(List.of(getAlbum(1L, 1L, Visibility.ALL_USERS),
                getAlbum(2L, 1L, Visibility.FOLLOWERS)));
        when(allUsersConverter.apply(getAlbum(1L, 1L, Visibility.ALL_USERS))).thenReturn(getAlbumResponseDto(1L));
        when(followersConverter.apply(getAlbum(2L, 1L, Visibility.FOLLOWERS))).thenReturn(getAlbumResponseDto(2L));

        List<AlbumResponseDto> result = albumService.getAlbumsByAuthorId(authorId);

        assertEquals(2, result.size());
        verify(albumRepository).findByAuthorId(authorId);
        verify(allUsersConverter).apply(getAlbum(1L,1L, Visibility.ALL_USERS));
        verify(followersConverter).apply(getAlbum(2L, 1L, Visibility.FOLLOWERS));
    }

    @Test
    public void testChangeVisibilityAlbum() {
        long albumId = 1L;
        long userId = 1L;
        Album album = getAlbum(1L, 1L, Visibility.ALL_USERS);
        when(albumRepository.findById(albumId))
                .thenReturn(Optional.of(album));
        when(userContext.getUserId()).thenReturn(userId);

        albumService.changeVisibilityAlbum(albumId, Visibility.FOLLOWERS);

        assertEquals(Visibility.FOLLOWERS, album.getVisibility());
        verify(albumRepository).save(getAlbum(1L, 1L, Visibility.FOLLOWERS));
    }

    @Test
    public void testChangeVisibilityAlbum_UserIsNotAuthor() {
        long albumId = 1L;
        long userId = 1L;
        long otherUserId = 2L;
        Album album = getAlbum(albumId, otherUserId, Visibility.ONLY_AUTHOR);
        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));
        when(userContext.getUserId()).thenReturn(userId);

        assertThrows(AlbumAccessDeniedException.class,
                () -> albumService.changeVisibilityAlbum(albumId, Visibility.FOLLOWERS));
        verify(albumRepository, never()).save(album);
    }

    @Test
    public void testAddUsersForAccessAlbum() {
        long albumId = 1L;
        long userId = 1L;
        Album album = getAlbum(albumId, userId, Visibility.SELECTED_USERS);
        AlbumUsersDto albumUsersDto = new AlbumUsersDto(List.of(2L, 3L));
        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));
        when(userContext.getUserId()).thenReturn(userId);

        albumService.addUsersForAccessAlbum(albumId, albumUsersDto);

        verify(albumRepository).addUserForVisibilityAtAlbum(albumId, 2L);
        verify(albumRepository).addUserForVisibilityAtAlbum(albumId, 3L);
    }

    @Test
    public void testAddUsersForAccessAlbum_WrongVisibility() {
        long albumId = 1L;
        long userId = 1L;
        Album album = getAlbum(albumId, userId, Visibility.ALL_USERS);
        AlbumUsersDto albumUsersDto = new AlbumUsersDto(List.of(2L, 3L));
        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));
        when(userContext.getUserId()).thenReturn(userId);

        assertThrows(IllegalArgumentException.class, () -> albumService.addUsersForAccessAlbum(albumId, albumUsersDto));
        verify(albumRepository, never()).addUserForVisibilityAtAlbum(anyLong(), anyLong());
    }

    private AlbumResponseDto getAlbumResponseDto(long albumId) {
        return new AlbumResponseDto(albumId, "album", null, 1L, List.of());
    }

    private static Album getAlbum(long albumId, long authorId, Visibility visibility) {
        return Album.builder()
                .id(albumId)
                .visibility(visibility)
                .authorId(authorId)
                .build();
    }

}