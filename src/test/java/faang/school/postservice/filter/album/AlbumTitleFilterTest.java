package faang.school.postservice.filter.album;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.filters.album.impl.AlbumTitleFilter;
import faang.school.postservice.model.Album;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class AlbumTitleFilterTest {

    private final AlbumTitleFilter filter = new AlbumTitleFilter();
    private AlbumFilterDto filterDto;
    private Album album1;
    private Album album2;
    Stream<Album> stream;

    @BeforeEach
    public void init() {
        filterDto = new AlbumFilterDto();
        album1 = Album.builder()
                .id(1L)
                .title("First album")
                .description("Desc")
                .build();
        album2 = Album.builder()
                .id(2L)
                .title("Second album")
                .description("Desc")
                .build();
        stream = Stream.of(album1, album2);
    }

    @Test
    public void testApplySuccessCase() {
        filterDto.setTitlePattern("First");
        List<Album> actual = filter.apply(stream, filterDto).toList();
        assertEquals(1, actual.size());
        assertEquals(album1, actual.get(0));
    }

    @Test
    public void testApplyCaseWithNotFullString() {
        filterDto.setTitlePattern("a");
        List<Album> actual = filter.apply(stream, filterDto).toList();
        assertEquals(2, actual.size());
    }

    @Test
    public void testApplyWithTitlePatternNull() {
        List<Album> actual = filter.apply(stream, filterDto).toList();
        assertEquals(2, actual.size());
    }

    @Test
    public void testApplyWithBlankString() {
        filterDto.setTitlePattern("");
        List<Album> actual = filter.apply(stream, filterDto).toList();
        assertEquals(2, actual.size());
    }

    @Test
    public void testApplyWithNoMatch() {
        filterDto.setTitlePattern("Nonexistent");
        List<Album> actual = filter.apply(stream, filterDto).toList();
        assertEquals(0, actual.size());
    }
}
