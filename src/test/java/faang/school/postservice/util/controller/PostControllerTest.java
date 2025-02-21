package faang.school.postservice.util.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.post.RequestPostDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
public class PostControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCreatePostByUserId() throws Exception {
        Long userId = 1L;
        RequestPostDto requestPostDto = new RequestPostDto();
        requestPostDto.setContent("Content");

        mockMvc.perform(post("/post/create-by-user/{user-id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestPostDto)))
                .andExpect(status().isCreated());
    }

    @Test
    public void testCreatePostByUserId_WithBlankContent() throws Exception {
        Long userId = 1L;
        RequestPostDto requestPostDto = new RequestPostDto();
        requestPostDto.setContent(" ");

        mockMvc.perform(post("/post/create-by-user/{user-id}", userId)
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
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestPostDto)))
                .andExpect(status().isBadRequest());
    }
}