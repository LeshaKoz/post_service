package faang.school.postservice.filter.album;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.model.Album;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class DateFilterTest {
    private static final LocalDate DATE = LocalDate.of(2025, 1, 1);

    private static final LocalDate OTHER_DATE = LocalDate.of(2025, 2, 1);

    private final DateFilter dateFilter = new DateFilter();

    @Test
    void testIsApplicableIsTrue() {
        AlbumFilterDto filter = AlbumFilterDto.builder()
                .fromDate(DATE)
                .build();

        assertTrue(dateFilter.isApplicable(filter));
    }


    @Test
    void testIsApplicableIsFalse() {
        AlbumFilterDto filter = AlbumFilterDto.builder().build();

        assertFalse(dateFilter.isApplicable(filter));
    }

    @Test
    void testApplyIsSuccess() {
        AlbumFilterDto filter = AlbumFilterDto.builder()
                .fromDate(DATE)
                .build();

        Album album = Album.builder()
                .createdAt(DATE.atStartOfDay())
                .build();

        Stream<Album> albumStream = Stream.of(album);
        Stream<Album> result = dateFilter.apply(albumStream, filter);

        assertFalse(result.toList().isEmpty());
    }

    @Test
    void testApplyIsNotSuccess() {
        AlbumFilterDto filter = AlbumFilterDto.builder()
                .fromDate(DATE)
                .build();

        Album album = Album.builder()
                .createdAt(OTHER_DATE.atStartOfDay())
                .build();

        Stream<Album> albumStream = Stream.of(album);
        Stream<Album> result = dateFilter.apply(albumStream, filter);

        assertTrue(result.toList().isEmpty());
    }
}