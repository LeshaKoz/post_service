package faang.school.postservice.controller;

import faang.school.postservice.service.LikeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ContextConfiguration(classes = {LikeController.class})
public class LikeControllerTest {

    private final Long firstId = 1L;

    @MockBean
    private LikeService likeService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testPositivePutLikeOnPost() throws Exception {
        doNothing().when(likeService).putLikeOnPost(firstId, firstId);

        mockMvc.perform(post("/likes/post-{postId}", firstId)
                .param("userId", firstId.toString()))
                .andExpect(status().isOk());
    }

    @Test
    public void testPositiveRemoveLikeOnPost() throws Exception {
        doNothing().when(likeService).removeLikeAtPost(firstId, firstId);

        mockMvc.perform(delete("/likes/post-{postId}", firstId)
                        .param("userId", firstId.toString()))
                .andExpect(status().isOk());
    }

    @Test
    public void testPositivePutLikeOnComment() throws Exception {
        doNothing().when(likeService).putLikeOnComment(firstId, firstId);

        mockMvc.perform(post("/likes/comment-{commentId}", firstId)
                        .param("userId", firstId.toString()))
                .andExpect(status().isOk());
    }

    @Test
    public void testPositiveRemoveLikeOnComment() throws Exception {
        doNothing().when(likeService).removeLikeAtComment(firstId, firstId);

        mockMvc.perform(delete("/likes/comment-{commentId}", firstId)
                        .param("userId", firstId.toString()))
                .andExpect(status().isOk());
    }
}
