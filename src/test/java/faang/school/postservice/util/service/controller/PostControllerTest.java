package faang.school.postservice.util.service.controller;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import faang.school.postservice.controller.PostController;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.service.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration(classes = {PostController.class, PostService.class})
@WebMvcTest
public class PostControllerTest {
    private final String REJECT_POST_URL = "/posts";

    private final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    @MockBean
    private PostService postService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void test() throws Exception {
        OBJECT_MAPPER.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        mockMvc.perform(post(REJECT_POST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(OBJECT_MAPPER.writeValueAsString(preparePostDto()))
        ).andExpect(status().isOk());
    }

    private PostDto preparePostDto() {
       return PostDto.builder()
               .id(1L)
               .authorId(1L)
               .content("content")
               .build();
    }
}
