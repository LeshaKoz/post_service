package faang.school.postservice.util.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.RequestPostDto;
import faang.school.postservice.dto.user.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
public class PostControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserServiceClient userServiceClient;

    @BeforeEach
    public void setup() {
        when(userServiceClient.getUser(anyLong()))
                .thenReturn(new UserDto(1L, "user", "user@mail"));
    }


    @Test
    public void testCreatePostByUserId() throws Exception {
        Long userId = 1L;
        RequestPostDto requestPostDto = new RequestPostDto();
        requestPostDto.setContent("Content");

        mockMvc.perform(post("/post/create-by-user/{user-id}", userId)
                        .header("x-user-id", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("x-user-id", userId.toString())
                        .content(objectMapper.writeValueAsString(requestPostDto)))
                .andExpect(status().isCreated());
    }

    @Test
    public void testCreatePostByUserId_WithBlankContent() throws Exception {
        Long userId = 1L;
        RequestPostDto requestPostDto = new RequestPostDto();
        requestPostDto.setContent(" ");

        mockMvc.perform(post("/post/create-by-user/{user-id}", userId)
                        .header("x-user-id", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestPostDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreatePostByUserId_WithNullContent() throws Exception {
        Long userId = 1L;
        RequestPostDto requestPostDto = new RequestPostDto();
        requestPostDto.setContent(null);

        mockMvc.perform(post("/post/create-by-user/{user-id}", userId)
                        .header("x-user-id", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestPostDto)))
                .andExpect(status().isBadRequest());
    }
}