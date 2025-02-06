package faang.school.postservice.controller.post;

import faang.school.postservice.config.context.UserHeaderFilter;
import faang.school.postservice.dto.post.HashtagRequestDto;
import faang.school.postservice.dto.post.HashtagResponseDto;
import faang.school.postservice.service.post.HashtagService;
import faang.school.postservice.service.post.HashtagServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HashtagServiceImpl.class)
class HashtagControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HashtagService service;

    @MockBean
    private UserHeaderFilter filter;

    @Test
    public void testGetAllHashtags() throws Exception {
        HashtagResponseDto hashtag = new HashtagResponseDto("hashtag", List.of());
        List<HashtagResponseDto> hashtags = List.of(hashtag);
        when(service.getAllHashtags()).thenReturn(hashtags);

        mockMvc.perform(get("/api/v1/hashtag"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetTopHashtags() throws Exception {
        HashtagResponseDto hashtag = new HashtagResponseDto("hashtag", List.of());
        List<HashtagResponseDto> hashtags = List.of(hashtag);
        when(service.getTopHashtags()).thenReturn(hashtags);

        mockMvc.perform(get("/api/v1/hashtag/top"))
                .andExpect(status().isOk());
    }

    @Test
    public void testAddHashtagToPost() throws Exception {
        HashtagRequestDto hashtag = new HashtagRequestDto(1L, "hashtag");
        doNothing().when(service).addHashtagToPost(hashtag);

        mockMvc.perform(post("/api/v1/hashtag"))
                .andExpect(status().isOk());
    }
}