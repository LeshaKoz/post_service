package faang.school.postservice.service.album.filter;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.model.Album;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class AlbumDateFilterTest {

    private final AlbumDateFilter albumDateFilter = new AlbumDateFilter();

    @Test
    void isApplicable_ReturnsFalseIfNoDates() {
        AlbumFilterDto filter = new AlbumFilterDto(null, null, null, null);
        assertFalse(albumDateFilter.isApplicable(filter));
    }

    @Test
    void isApplicable_ReturnsTrueIfFromDatePresent() {
        AlbumFilterDto filter = new AlbumFilterDto(null, LocalDateTime.now(), null, null);
        assertTrue(albumDateFilter.isApplicable(filter));
    }

    @Test
    void isApplicable_ReturnsTrueIfToDatePresent() {
        AlbumFilterDto filter = new AlbumFilterDto(null, null, LocalDateTime.now(), null);
        assertTrue(albumDateFilter.isApplicable(filter));
    }

    @Test
    void applyFilter_ByFromDate() {
        LocalDateTime fromDate = LocalDateTime.of(2020, 1, 1, 0, 0);
        AlbumFilterDto filter = new AlbumFilterDto(null, fromDate, null, null);

        Album album1 = new Album();
        album1.setId(1L);
        album1.setCreatedAt(LocalDateTime.of(2020, 1, 2, 0, 0));

        Album album2 = new Album();
        album2.setId(2L);
        album2.setCreatedAt(LocalDateTime.of(2019, 12, 31, 23, 59));

        List<Album> result = albumDateFilter.apply(Stream.of(album1, album2), filter)
                .toList();
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    void applyFilter_ByToDate() {
        LocalDateTime toDate = LocalDateTime.of(2020, 1, 1, 0, 0);
        AlbumFilterDto filter = new AlbumFilterDto(null, null, toDate, null);

        Album album1 = new Album();
        album1.setId(1L);
        album1.setCreatedAt(LocalDateTime.of(2019, 12, 31, 23, 59));

        Album album2 = new Album();
        album2.setId(2L);
        album2.setCreatedAt(LocalDateTime.of(2020, 1, 1, 0, 1));

        List<Album> result = albumDateFilter.apply(Stream.of(album1, album2), filter)
                .toList();
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    void applyFilter_ByFromAndToDate() {
        LocalDateTime fromDate = LocalDateTime.of(2020, 1, 1, 0, 0);
        LocalDateTime toDate = LocalDateTime.of(2020, 12, 31, 23, 59);
        AlbumFilterDto filter = new AlbumFilterDto(null, fromDate, toDate, null);

        Album album1 = new Album();
        album1.setId(1L);
        album1.setCreatedAt(LocalDateTime.of(2020, 6, 15, 12, 0));

        Album album2 = new Album();
        album2.setId(2L);
        album2.setCreatedAt(LocalDateTime.of(2019, 12, 31, 23, 59));

        Album album3 = new Album();
        album3.setId(3L);
        album3.setCreatedAt(LocalDateTime.of(2021, 1, 1, 0, 0));

        List<Album> result = albumDateFilter.apply(Stream.of(album1, album2, album3), filter)
                .toList();
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }
}
