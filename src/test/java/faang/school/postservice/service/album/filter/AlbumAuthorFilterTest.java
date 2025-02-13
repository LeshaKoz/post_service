package faang.school.postservice.service.album.filter;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.model.Album;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class AlbumAuthorFilterTest {

    private final AlbumAuthorFilter albumAuthorFilter = new AlbumAuthorFilter();

    @Test
    void isApplicable_ReturnsFalseIfAuthorIdNull() {
        AlbumFilterDto filter = new AlbumFilterDto(null, null, null, null);
        assertFalse(albumAuthorFilter.isApplicable(filter));
    }

    @Test
    void isApplicable_ReturnsTrueIfAuthorIdPresent() {
        AlbumFilterDto filter = new AlbumFilterDto(null, null, null, 100L);
        assertTrue(albumAuthorFilter.isApplicable(filter));
    }

    @Test
    void applyFilter_ByAuthorId() {
        AlbumFilterDto filter = new AlbumFilterDto(null, null, null, 100L);

        Album album1 = new Album();
        album1.setId(1L);
        album1.setAuthorId(100L);

        Album album2 = new Album();
        album2.setId(2L);
        album2.setAuthorId(200L);

        List<Album> result = albumAuthorFilter.apply(Stream.of(album1, album2), filter)
                .toList();
        assertEquals(1, result.size());
        assertEquals(100L, result.get(0).getAuthorId());
    }
}
