package faang.school.postservice.controller.post;

import faang.school.postservice.config.context.UserHeaderFilter;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.service.post.PostServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostServiceImpl.class)
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostServiceImpl service;

    @MockBean
    private UserHeaderFilter filter;

    @Test
    public void testGetTopHashtags() throws Exception {
        List<PostResponseDto> posts = List.of(PostResponseDto.builder().content("content").build());
        when(service.getPostsByHashtag(eq("hashtag"))).thenReturn(posts);

        mockMvc.perform(get("/api/v1/posts"))
                .andExpect(status().isOk());
    }

}