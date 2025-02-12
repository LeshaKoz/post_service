package faang.school.postservice.filter.album;


import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.filters.album.impl.AlbumDateFilter;
import faang.school.postservice.model.Album;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class AlbumDateFilterTest {
    private final AlbumDateFilter filter = new AlbumDateFilter();
    private AlbumFilterDto filterDto;
    private Album album1;
    private Album album2;
    Stream<Album> stream;

    @BeforeEach
    public void init() {
        filterDto = new AlbumFilterDto();
        LocalDateTime now = LocalDateTime.now();
        filterDto.setFromDatePattern(now.minusDays(5));
        filterDto.setToDatePattern(now.plusDays(5));
        album1 = Album.builder()
                .id(1L)
                .title("Rock Classics")
                .description("Desc")
                .authorId(1L)
                .createdAt(now.minusDays(3))
                .updatedAt(now)
                .build();
        album2 = Album.builder()
                .id(2L)
                .title("Jazz Vibes")
                .description("Desc")
                .authorId(1L)
                .createdAt(now.minusDays(10))
                .updatedAt(now)
                .build();
        stream = Stream.of(album1, album2);
    }

    @Test
    public void testApplyDateRange() {
        List<Album> actual = filter.apply(stream, filterDto).toList();
        assertEquals(1, actual.size());
        assertEquals(album1, actual.get(0));
    }
}
