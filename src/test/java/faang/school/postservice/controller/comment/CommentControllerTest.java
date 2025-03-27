package faang.school.postservice.controller.comment;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.service.comment.CommentService;
import org.junit.jupiter.api.Test;
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
    void createComment_shouldCreateCommentWhenValidDataProvided() throws Exception {
        CommentDto commentDto = CommentDto.builder()
                .content("Test comment")
                .authorId(AUTHOR_ID)
                .postId(POST_ID)
                .build();

        CommentDto returnedCommentDto = CommentDto.builder()
                .id(1L)
                .content("Test comment")
                .authorId(AUTHOR_ID)
                .postId(POST_ID)
                .build();

        when(commentService.createComment(commentDto)).thenReturn(returnedCommentDto);
        mockMvc.perform(post("/api/v1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.content").value("Test comment"))
                .andExpect(jsonPath("$.authorId").value(AUTHOR_ID))
                .andExpect(jsonPath("$.postId").value(POST_ID));

        verify(commentService, times(1)).createComment(eq(commentDto));
    }

    @Test
    void createComment_shouldReturnBadRequestWhenNoBodyProvided() throws Exception {
        mockMvc.perform(post("/api/v1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllCommentsByPostId_shouldReturnAllCommentsForPostWhenPostIdIsValid() throws Exception {
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
    void getAllCommentsByPostId_shouldReturnEmptyListWhenNoCommentsForPost() throws Exception {
        long postId = 999L;

        when(commentService.getAllCommentsByPostId(postId)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/comments/post/{postId}", postId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(commentService, times(1)).getAllCommentsByPostId(postId);
    }

    @Test
    void updateComment_shouldUpdateCommentWhenValidDataProvided() throws Exception {
        CommentDto commentDto = CommentDto.builder()
                .id(COMMENT_ID)
                .content("Updated comment")
                .authorId(AUTHOR_ID)
                .postId(POST_ID)
                .build();

        when(commentService.updateComment(COMMENT_ID, commentDto)).thenReturn(commentDto);

        mockMvc.perform(put("/api/v1/comments/{commentId}", COMMENT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(COMMENT_ID))
                .andExpect(jsonPath("$.content").value("Updated comment"))
                .andExpect(jsonPath("$.authorId").value(AUTHOR_ID))
                .andExpect(jsonPath("$.postId").value(POST_ID));

        verify(commentService, times(1)).updateComment(COMMENT_ID, commentDto);
    }

    @Test
    void updateComment_shouldReturnBadRequestWhenInvalidId() throws Exception {
        CommentDto commentDto = CommentDto.builder()
                .id(-1L)  // или 0L
                .content("Updated comment")
                .authorId(AUTHOR_ID)
                .postId(POST_ID)
                .build();

        mockMvc.perform(put("/api/v1/comments/{commentId}", -1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteComment_shouldDeleteCommentWhenValidIdsProvided() throws Exception {
        mockMvc.perform(delete("/api/v1/comments/{commentId}", COMMENT_ID))
                .andExpect(status().isNoContent());

        verify(commentService, times(1)).deleteComment(COMMENT_ID);
    }

    @Test
    void deleteComment_shouldReturnBadRequestWhenIdIsInvalid() throws Exception {
        mockMvc.perform(delete("/api/v1/comments/{commentId}", -1L))
                .andExpect(status().isBadRequest());
    }
}