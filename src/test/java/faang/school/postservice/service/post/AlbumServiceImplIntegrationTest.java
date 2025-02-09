package faang.school.postservice.service.post;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.AlbumResponseDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.enums.Visibility;
import faang.school.postservice.exception.album.AlbumAccessDeniedException;
import faang.school.postservice.model.Album;
import faang.school.postservice.repository.post.AlbumRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@SpringBootTest()
@Testcontainers
class AlbumServiceImplIntegrationTest {

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private AlbumService albumService;

    @MockBean
    private UserContext userContext;

    @MockBean
    private UserServiceClient userServiceClient;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeEach
    public void setUp() {
        when(userContext.getUserId()).thenReturn(1L);
        when(userServiceClient.getFollowersByUserId(1L)).thenReturn(
                List.of(getUserDto(1L), getUserDto(2L), getUserDto(3L)));
        albumRepository.deleteAll();
    }

    @Test
    public void testGetAlbumById() {
        Album album = getAlbum("Test Album", Visibility.ALL_USERS, 1L);
        albumRepository.save(album);

        AlbumResponseDto result = albumService.getAlbumById(album.getId());

        assertNotNull(result);
        assertEquals(album.getId(), result.id());
        assertEquals("Test Album", result.title());
    }

    @Test
    public void testGetAlbumsByAuthorId() {
        long authorId = 1L;
        Album album1 = getAlbum("Album 1", Visibility.ALL_USERS, authorId);
        Album album2 = getAlbum("Album 2", Visibility.FOLLOWERS, authorId);

        albumRepository.saveAll(List.of(album1, album2));

        List<AlbumResponseDto> result = albumService.getAlbumsByAuthorId(authorId);

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(a -> a.title().equals("Album 1")));
        assertTrue(result.stream().anyMatch(a -> a.title().equals("Album 2")));
    }

    @Test
    public void testChangeVisibilityAlbum() {
        Album album = getAlbum("Test Album", Visibility.ALL_USERS, 1L);
        albumRepository.save(album);

        albumService.changeVisibilityAlbum(album.getId(), Visibility.FOLLOWERS);

        Album updatedAlbum = albumRepository.findById(album.getId()).orElseThrow();
        assertEquals(Visibility.FOLLOWERS, updatedAlbum.getVisibility());
    }

    @Test
    public void testGetAlbumById_NotFound() {
        assertThrows(EntityNotFoundException.class, () -> albumService.getAlbumById(999L));
    }

    @Test
    public void testChangeVisibilityAlbumWhenNotAuthor() {
        Album album = getAlbum("Test Album", Visibility.ALL_USERS, 2L);
        albumRepository.save(album);

        assertThrows(AlbumAccessDeniedException.class,
                () -> albumService.changeVisibilityAlbum(album.getId(), Visibility.FOLLOWERS));
    }

    private UserDto getUserDto(long id) {
        return new UserDto(id, null, null, null);
    }

    private Album getAlbum(String title, Visibility visibility, long authorId) {
        Album album = new Album();
        album.setTitle(title);
        album.setVisibility(visibility);
        album.setAuthorId(authorId);
        return album;
    }

}