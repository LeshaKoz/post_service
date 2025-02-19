package faang.school.postservice.filter.album;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.model.Album;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class TitleFilterTest {
    private static final String TITLE = "Memories";

    private static final String OTHER_TITLE = "Peace";

    private final TitleFilter titleFilter = new TitleFilter();

    @Test
    void testIsApplicableIsTrue() {
        AlbumFilterDto filter = AlbumFilterDto.builder()
                .titlePattern(TITLE)
                .build();

        assertTrue(titleFilter.isApplicable(filter));
    }

    @Test
    void testIsApplicableIsFalse() {
        AlbumFilterDto filter = AlbumFilterDto.builder().build();

        assertFalse(titleFilter.isApplicable(filter));
    }

    @Test
    void testApplyIsSuccess() {
        AlbumFilterDto filter = AlbumFilterDto.builder()
                .titlePattern(TITLE)
                .build();

        Album album = Album.builder()
                .title(TITLE)
                .build();

        Stream<Album> albumStream = Stream.of(album);
        Stream<Album> result = titleFilter.apply(albumStream, filter);

        assertFalse(result.toList().isEmpty());
    }

    @Test
    void testApplyIsNotSuccess() {
        AlbumFilterDto filter = AlbumFilterDto.builder()
                .titlePattern(TITLE)
                .build();

        Album album = Album.builder()
                .title(OTHER_TITLE)
                .build();

        Stream<Album> albumStream = Stream.of(album);
        Stream<Album> result = titleFilter.apply(albumStream, filter);

        assertTrue(result.toList().isEmpty());
    }
}