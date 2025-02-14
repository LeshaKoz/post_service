package faang.school.postservice.service.album.filter;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.model.Album;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class AlbumTitleFilterTest {

    private final AlbumTitleFilter albumTitleFilter = new AlbumTitleFilter();

    @Test
    void isApplicable_ReturnsFalseIfTitlePatternNullOrBlank() {
        AlbumFilterDto filter1 = new AlbumFilterDto(null, null, null, null);
        AlbumFilterDto filter2 = new AlbumFilterDto("  ", null, null, null);
        assertFalse(albumTitleFilter.isApplicable(filter1));
        assertFalse(albumTitleFilter.isApplicable(filter2));
    }

    @Test
    void isApplicable_ReturnsTrueIfTitlePatternPresent() {
        AlbumFilterDto filter = new AlbumFilterDto("Rock.*", null, null, null);
        assertTrue(albumTitleFilter.isApplicable(filter));
    }

    @Test
    void applyFilter_ByTitlePattern() {
        AlbumFilterDto filter = new AlbumFilterDto("Rock.*", null, null, null);

        Album album1 = new Album();
        album1.setId(1L);
        album1.setTitle("Rock Album");

        Album album2 = new Album();
        album2.setId(2L);
        album2.setTitle("Jazz Album");

        List<Album> result = albumTitleFilter.apply(Stream.of(album1, album2), filter)
                .toList();
        assertEquals(1, result.size());
        assertEquals("Rock Album", result.get(0).getTitle());
    }
}

