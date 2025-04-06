package faang.school.postservice.controller;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.PostReadDto;
import faang.school.postservice.exception.GlobalExceptionHandler;
import faang.school.postservice.service.FeedService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ContextConfiguration(classes = {FeedController.class, FeedService.class, GlobalExceptionHandler.class})
class FeedControllerTest {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FeedService feedService;

    @MockBean
    private UserContext userContext;

    @Test
    void testGetUserFeed() throws Exception {
        long userId = 1L;
        Long postId = 10L;
        List<PostReadDto> expectedFeed = List.of(
                new PostReadDto(), new PostReadDto()
        );

        when(userContext.getUserId()).thenReturn(userId);
        when(feedService.getUserFeed(userId, postId)).thenReturn(expectedFeed);

        mockMvc.perform(get("/v1/posts/feed")
                        .param("postId", String.valueOf(postId)))
                .andExpect(status().isOk())
                .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(expectedFeed)));

        verify(userContext).getUserId();
        verify(feedService).getUserFeed(userId, postId);
    }

    @Test
    void testInitFeedHeater() throws Exception {
        mockMvc.perform(get("/v1/posts/heat"))
                .andExpect(status().isOk());

        verify(feedService).initFeedHeater();
    }

    @Test
    void testGetUserFeedFeedServiceThrowsException() throws Exception {
        long userId = 1L;
        when(userContext.getUserId()).thenReturn(userId);
        when(feedService.getUserFeed(userId, null))
                .thenThrow(new RuntimeException("Feed service failed"));

        mockMvc.perform(get("/v1/posts/feed"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Непредвиденная ошибка сервера"))
                .andExpect(jsonPath("$.message").value("Feed service failed"))
                .andExpect(jsonPath("$.status").value(500));
    }

    @Test
    void testGetUserFeedUserContextFails() throws Exception {
        when(userContext.getUserId())
                .thenThrow(new IllegalStateException("No user in context"));

        mockMvc.perform(get("/v1/posts/feed"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Непредвиденная ошибка сервера"))
                .andExpect(jsonPath("$.message").value("No user in context"))
                .andExpect(jsonPath("$.status").value(500));
    }

    @Test
    void testInitFeedHeaterFeedServiceFails() throws Exception {
        doThrow(new RuntimeException("Feed init error"))
                .when(feedService).initFeedHeater();

        mockMvc.perform(get("/v1/posts/heat"))
                .andExpect(status().isInternalServerError());

        verify(feedService).initFeedHeater();
    }
}
