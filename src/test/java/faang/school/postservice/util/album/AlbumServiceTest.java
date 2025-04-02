package faang.school.postservice.util.album;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.album.AlbumMapperImpl;
import faang.school.postservice.mapper.album.PostMapperImpl;
import faang.school.postservice.model.Album;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.album.AlbumRepository;
import faang.school.postservice.service.album.impl.AlbumServiceImpl;
import org.apache.catalina.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AlbumServiceTest {

    @Mock
    private AlbumRepository albumRepository = Mockito.mock(AlbumRepository.class);
    @Mock
    private UserServiceClient userServiceClient = Mockito.mock(UserServiceClient.class);
    @Spy
    private AlbumMapperImpl albumMapper;
    @Spy
    private PostMapperImpl postMapper;
    @Mock
    private PostRepository postRepository = Mockito.mock(PostRepository.class);

    private final AlbumServiceImpl albumServiceimpl = new AlbumServiceImpl(
            albumRepository,
            userServiceClient,
            new AlbumMapperImpl(),
            postMapper,
            postRepository);

    @Test
    public void createAlbum_success() {
        long userId = 1L;
        UserDto userDto = new UserDto(userId, "test", "test");
        AlbumDto dto = new AlbumDto();
        Album album = Album.builder()
                .authorId(userId)
                .build();
        Album savedAlbum = Album.builder()
                .id(1L)
                .authorId(userId)
                .build();
        when(userServiceClient.getUser(userId)).thenReturn(userDto);
        when(albumRepository.findAlbumByAuthorId(userId)).thenReturn(null);
        when(albumRepository.save(album)).thenReturn(savedAlbum);

        AlbumDto albumDto = albumServiceimpl.createAlbum(userId, dto);

        verify(albumRepository, times(1)).findAlbumByAuthorId(userId);
        verify(albumMapper, times(1)).toEntity(albumDto);
        verify(albumRepository, times(1)).save(album);
        verify(albumMapper, times(1)).toDto(savedAlbum);
        dto.setId(1L);
        dto.setAuthorId(userId);
        assertEquals(dto, albumDto);
    }
}
