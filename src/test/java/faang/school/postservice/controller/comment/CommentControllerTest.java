package faang.school.postservice.controller.comment;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.comment.CommentCreateDto;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CommentUpdateDto;
import faang.school.postservice.service.comment.CommentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.List;

@WebMvcTest(CommentController.class)
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    @MockBean
    private UserContext userContext;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final Long POST_ID = 1L;
    private static final Long AUTHOR_ID = 1L;
    private static final Long COMMENT_ID = 1L;

    @Test
    void createComment_shouldReturn400WhenContentEmpty() throws Exception {
        CommentCreateDto invalidComment = CommentCreateDto.builder()
                .content("")
                .authorId(AUTHOR_ID)
                .postId(POST_ID)
                .build();

        mockMvc.perform(post("/api/v1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidComment)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.content").value("Content cannot be blank."));
    }

    @Test
    void createComment_shouldReturn400WhenContentTooLong() throws Exception {
        String sizedContent = "a".repeat(5000);
        CommentCreateDto invalidComment = CommentCreateDto.builder()
                .content(sizedContent)
                .authorId(AUTHOR_ID)
                .postId(POST_ID)
                .build();

        mockMvc.perform(post("/api/v1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidComment)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.content").value("Content cannot exceed 4096 characters."));
    }

    @Test
    void getAllCommentsByPostId_shouldReturnCommentsWhenPostIdValid() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setContent("Test comment");
        commentDto.setPostId(POST_ID);

        List<CommentDto> comments = Collections.singletonList(commentDto);

        when(commentService.getAllCommentsByPostId(POST_ID)).thenReturn(comments);

        mockMvc.perform(get("/api/v1/comments/post/{postId}", POST_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("Test comment"))
                .andExpect(jsonPath("$[0].postId").value(POST_ID));

        verify(commentService, times(1)).getAllCommentsByPostId(POST_ID);
    }

    @Test
    void getAllCommentsByPostId_shouldReturnEmptyListWhenNoComments() throws Exception {
        long postId = 999L;

        when(commentService.getAllCommentsByPostId(postId)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/comments/post/{postId}", postId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(commentService, times(1)).getAllCommentsByPostId(postId);
    }

    @Test
    void updateComment_shouldUpdateCommentWhenDataValid() throws Exception {
        CommentUpdateDto commentUpdateDto = CommentUpdateDto.builder()
                .content("Updated comment")
                .authorId(AUTHOR_ID)
                .build();

        CommentDto updatedComment = CommentDto.builder()
                .id(COMMENT_ID)
                .content("Updated comment")
                .authorId(AUTHOR_ID)
                .postId(POST_ID)
                .build();

        when(commentService.updateComment(eq(COMMENT_ID), eq(commentUpdateDto))).thenReturn(updatedComment);

        mockMvc.perform(put("/api/v1/comments/{commentId}", COMMENT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(COMMENT_ID))
                .andExpect(jsonPath("$.content").value("Updated comment"))
                .andExpect(jsonPath("$.authorId").value(AUTHOR_ID))
                .andExpect(jsonPath("$.postId").value(POST_ID));

        verify(commentService, times(1)).updateComment(eq(COMMENT_ID), eq(commentUpdateDto));
    }

    @ParameterizedTest
    @ValueSource(longs = {0, -1})
    void updateComment_shouldReturn400WhenInvalidId(long invalidCommentId) throws Exception {
        CommentDto commentDto = CommentDto.builder()
                .id(invalidCommentId)
                .content("Updated comment")
                .authorId(AUTHOR_ID)
                .postId(POST_ID)
                .build();

        mockMvc.perform(put("/api/v1/comments/{commentId}", invalidCommentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteComment_shouldDeleteCommentWhenIdsValid() throws Exception {
        mockMvc.perform(delete("/api/v1/comments/{commentId}", COMMENT_ID)
                        .param("authorId", String.valueOf(AUTHOR_ID)))
                .andExpect(status().isNoContent());

        verify(commentService, times(1)).deleteComment(COMMENT_ID, AUTHOR_ID);
    }

    @Test
    void deleteComment_shouldReturn400WhenIdInvalid() throws Exception {
        mockMvc.perform(delete("/api/v1/comments/{commentId}", -1L)
                        .param("authorId", String.valueOf(AUTHOR_ID)))
                .andExpect(status().isBadRequest());
    }
}