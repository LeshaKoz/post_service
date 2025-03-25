package faang.school.postservice.filter.album;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.filters.album.impl.AlbumFavoriteFilter;
import faang.school.postservice.model.Album;
import faang.school.postservice.repository.AlbumRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AlbumFavoriteFilterTest {
    private Stream<Album> stream;
    private AlbumFavoriteFilter filter;
    private AlbumRepository albumRepository;
    private UserContext userContext;
    private AlbumFilterDto filterDto;
    private Album album1;
    private Album album2;

    @BeforeEach
    public void init() {
        albumRepository = mock(AlbumRepository.class);
        userContext = mock(UserContext.class);
        filter = new AlbumFavoriteFilter(albumRepository, userContext);
        filterDto = new AlbumFilterDto();
        filterDto.setIsFavoritePattern(true);
        album1 = Album.builder()
                .id(1L)
                .title("Rock Classics")
                .description("Desc")
                .authorId(1L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        album2 = Album.builder()
                .id(2L)
                .title("Jazz Vibes")
                .description("Desc")
                .authorId(1L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        stream = Stream.of(album1, album2);
    }

    @Test
    public void testApplyFavoriteCase() {
        when(userContext.getUserId()).thenReturn(1L);
        when(albumRepository.isFavorite(1L, 1L)).thenReturn(true);
        when(albumRepository.isFavorite(2L, 1L)).thenReturn(false);

        List<Album> actual = filter.apply(stream, filterDto).toList();
        assertEquals(1, actual.size());
        assertEquals(album1, actual.get(0));
    }
}
