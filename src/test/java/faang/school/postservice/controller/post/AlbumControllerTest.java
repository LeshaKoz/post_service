package faang.school.postservice.controller.post;

import faang.school.postservice.dto.post.AlbumResponseDto;
import faang.school.postservice.dto.post.AlbumUsersDto;
import faang.school.postservice.service.post.AlbumService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static faang.school.postservice.enums.Visibility.ALL_USERS;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(properties = {"domain.path=/api/v1"})
class AlbumControllerTest {

    private static final String BASE_URL = "/api/v1/albums";

    private MockMvc mockMvc;

    @Mock
    private AlbumService albumService;

    @InjectMocks
    private AlbumController albumController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
        Properties properties = new Properties();
        properties.setProperty("domain.path", "/api/v1");
        configurer.setProperties(properties);

        mockMvc = MockMvcBuilders.standaloneSetup(albumController)
                .addPlaceholderValue("domain.path", "/api/v1")
                .build();
    }

    @Test
    public void testFindAlbumById() throws Exception {
        when(albumService.getAlbumById(eq(1L))).thenReturn(
                new AlbumResponseDto(1L, "title", "descr", 1L, List.of())
        );

        mockMvc.perform(get(BASE_URL + "/{id}", 1L))
                .andDo(print())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(status().isOk());
    }

    @Test
    public void testFindAlbumsByAuthorId() throws Exception {
        when(albumService.getAlbumsByAuthorId(eq(1L))).thenReturn(
                List.of(new AlbumResponseDto(1L, "title", "descr", 1L, List.of()))
        );

        mockMvc.perform(get(BASE_URL + "/author/{authorId}", 1L))
                .andDo(print())
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(status().isOk());
    }

    @Test
    public void testChangeVisibilityAlbum() throws Exception {
        doNothing().when(albumService).changeVisibilityAlbum(eq(1L), eq(ALL_USERS));

        mockMvc.perform(put(BASE_URL + "/{id}/visibility/{visibility}", 1L, ALL_USERS))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void testAddUsersForAccessAlbum() throws Exception {
        AlbumUsersDto dto = new AlbumUsersDto(Arrays.asList(1L, 2L));
        doNothing().when(albumService).addUsersForAccessAlbum(eq(1L),
                argThat(arg -> arg.usersIds().equals(dto.usersIds())));

        mockMvc.perform(put(BASE_URL + "/{id}/add-users-for-access", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isOk());
    }

}