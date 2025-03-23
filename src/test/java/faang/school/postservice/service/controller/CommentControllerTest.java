package faang.school.postservice.service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.controller.comment.CommentController;
import faang.school.postservice.exception.AuthorNotFoundException;
import faang.school.postservice.exception.CommentNotFoundException;
import faang.school.postservice.exception.PostIdMismatchException;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.service.comment.interfaces.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    @MockBean
    private UserContext userContext;

    @Autowired
    private ObjectMapper objectMapper;

    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setContent("Test content");
        commentDto.setAuthorId(2L);
        commentDto.setPostId(1L);
        commentDto.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void createComment_success_returnsCreated() throws Exception {
        Long postId = 1L;

        CommentDto requestDto = new CommentDto();
        requestDto.setContent("Test content");
        requestDto.setAuthorId(2L);
        requestDto.setPostId(1L);
        when(commentService.createComment(postId, requestDto)).thenReturn(commentDto);

        mockMvc.perform(post("/posts/{postId}/comments", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.content").value("Test content"))
                .andExpect(jsonPath("$.authorId").value(2L))
                .andExpect(jsonPath("$.postId").value(1L));

        verify(commentService).createComment(postId, requestDto);
    }

    @Test
    void createComment_postIdMismatch_returnsBadRequest() throws Exception {
        Long postId = 1L;

        CommentDto requestDto = new CommentDto();
        requestDto.setContent("Test content");
        requestDto.setAuthorId(2L);
        requestDto.setPostId(3L);
        when(commentService.createComment(postId, requestDto))
                .thenThrow(new PostIdMismatchException("Post ID in path and DTO must match"));

        mockMvc.perform(post("/posts/{postId}/comments", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Post ID in path and DTO must match"));

        verify(commentService).createComment(postId, requestDto);
    }

    @Test
    void updateComment_success_returnsOk() throws Exception {
        Long postId = 1L;
        Long commentId = 1L;

        CommentDto requestDto = new CommentDto();
        requestDto.setContent("Updated content");
        requestDto.setAuthorId(2L);
        requestDto.setPostId(1L);
        when(commentService.updateComment(postId, commentId, requestDto)).thenReturn(commentDto);

        mockMvc.perform(put("/posts/{postId}/comments/{commentId}", postId, commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.content").value("Test content"))
                .andExpect(jsonPath("$.authorId").value(2L))
                .andExpect(jsonPath("$.postId").value(1L));

        verify(commentService).updateComment(postId, commentId, requestDto);
    }

    @Test
    void updateComment_commentNotFound_returnsNotFound() throws Exception {
        Long postId = 1L;
        Long commentId = 1L;

        CommentDto requestDto = new CommentDto();
        requestDto.setContent("Updated content");
        requestDto.setAuthorId(2L);
        requestDto.setPostId(1L);
        when(commentService.updateComment(postId, commentId, requestDto))
                .thenThrow(new CommentNotFoundException("Comment with id " + commentId + " not found"));

        mockMvc.perform(put("/posts/{postId}/comments/{commentId}", postId, commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Comment with id " + commentId + " not found"));

        verify(commentService).updateComment(postId, commentId, requestDto);
    }

    // Тесты для getComments
    @Test
    void getComments_success_returnsOk() throws Exception {
        Long postId = 1L;

        List<CommentDto> comments = List.of(commentDto);
        when(commentService.getCommentsByPostId(postId)).thenReturn(comments);

        mockMvc.perform(get("/posts/{postId}/comments", postId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].content").value("Test content"))
                .andExpect(jsonPath("$[0].authorId").value(2L))
                .andExpect(jsonPath("$[0].postId").value(1L));

        verify(commentService).getCommentsByPostId(postId);
    }

    @Test
    void getComments_emptyList_returnsOk() throws Exception {
        Long postId = 1L;

        when(commentService.getCommentsByPostId(postId)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/posts/{postId}/comments", postId))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(commentService).getCommentsByPostId(postId);
    }

    @Test
    void deleteComment_success_returnsNoContent() throws Exception {
        Long postId = 1L;
        Long commentId = 1L;

        doNothing().when(commentService).deleteComment(postId, commentId);

        mockMvc.perform(delete("/posts/{postId}/comments/{commentId}", postId, commentId))
                .andExpect(status().isNoContent());

        verify(commentService).deleteComment(postId, commentId);
    }

    @Test
    void deleteComment_authorNotFound_returnsNotFound() throws Exception {
        Long postId = 1L;
        Long commentId = 1L;

        doThrow(new AuthorNotFoundException("Author with id 2 not found"))
                .when(commentService).deleteComment(postId, commentId);

        mockMvc.perform(delete("/posts/{postId}/comments/{commentId}", postId, commentId))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Author with id 2 not found"));

        verify(commentService).deleteComment(postId, commentId);
    }
}