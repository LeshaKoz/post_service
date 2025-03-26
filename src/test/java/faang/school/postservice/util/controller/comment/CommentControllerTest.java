package faang.school.postservice.util.controller.comment;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.controller.comment.CommentController;
import faang.school.postservice.dto.comment.CommentCreateDto;
import faang.school.postservice.dto.comment.CommentViewDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.service.comment.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
public class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CommentService commentService;

    @MockBean
    private UserContext userContext;

    private CommentCreateDto commentCreateDto;
    private CommentViewDto commentViewDto;

    @BeforeEach
    void setUp() {
        commentCreateDto = new CommentCreateDto();
        commentCreateDto.setContent("content");
        commentCreateDto.setAuthorId(1L);
        commentCreateDto.setPostId(1L);

        commentViewDto = new CommentViewDto();
        commentViewDto.setId(1L);
        commentViewDto.setContent("content");
        commentViewDto.setAuthorId(1L);
        commentViewDto.setPostId(1L);
    }

    @Nested
    @DisplayName("POST /posts/{postId}/comments - Создание комментария")
    class CreateCommentTests {

        @Test
        @DisplayName("Должен успешно создать комментарий и вернуть CommentViewDto со статусом 200 OK")
        void givenValidRequest_whenCreateComment_thenReturnOkWithCommentData() throws Exception {
            Mockito.when(commentService.createComment(1L, commentCreateDto))
                    .thenReturn(commentViewDto);

            mockMvc.perform(post("/posts/{postId}/comments", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(commentCreateDto)))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Должен вернуть 404 Not Found при несуществующем postId")
        void givenNonExistentPostId_whenCreateComment_thenReturnNotFound() throws Exception {
            Mockito.when(commentService.createComment(1L, commentCreateDto))
                    .thenThrow(new EntityNotFoundException("Post with id 1 not found"));

            mockMvc.perform(post("/posts/{postId}/comments", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(commentCreateDto)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Должен вернуть 400 Bad Request при null-содержимом")
        void givenNullContent_whenCreateComment_thenReturnBadRequest() throws Exception {
            commentCreateDto.setContent(null);

            mockMvc.perform(post("/posts/{postId}/comments", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(commentCreateDto)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Должен вернуть 404 Not Found при несуществующем authorId")
        void givenNonExistentAuthorId_whenCreateComment_thenReturnNotFound() throws Exception {
            Mockito.when(commentService.createComment(1L, commentCreateDto))
                    .thenThrow(new EntityNotFoundException("Author with id 1 not found"));

            mockMvc.perform(post("/posts/{postId}/comments", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(commentCreateDto)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("PUT /posts/{postId}/comments/{commentId} - Обновление комментария")
    class UpdateCommentTests {

        @Test
        @DisplayName("Должен успешно обновить комментарий и вернуть CommentViewDto со статусом 200 OK")
        void givenValidRequest_whenUpdateComment_thenReturnOkWithUpdatedComment() throws Exception {
            Mockito.when(commentService.updateComment(1L, 1L, commentCreateDto))
                    .thenReturn(commentViewDto);

            mockMvc.perform(put("/posts/{postId}/comments/{commentId}", 1L, 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(commentCreateDto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.content").value("content"));
        }

        @Test
        @DisplayName("Должен вернуть 404 Not Found при несуществующем commentId")
        void givenNonExistentCommentId_whenUpdateComment_thenReturnNotFound() throws Exception {
            Mockito.when(commentService.updateComment(1L, 999L, commentCreateDto))
                    .thenThrow(new EntityNotFoundException("Comment not found"));

            mockMvc.perform(put("/posts/{postId}/comments/{commentId}", 1L, 999L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(commentCreateDto)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Должен вернуть 400 Bad Request при null-содержимом")
        void givenNullContent_whenUpdateComment_thenReturnBadRequest() throws Exception {
            commentCreateDto.setContent(null);

            mockMvc.perform(put("/posts/{postId}/comments/{commentId}", 1L, 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(commentCreateDto)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("GET /posts/{postId}/comments - Получение комментариев поста")
    class GetCommentsTests {

        @Test
        @DisplayName("Должен вернуть список комментариев со статусом 200 OK")
        void givenExistingPostId_whenGetComments_thenReturnOkWithCommentsList() throws Exception {
            List<CommentViewDto> comments = List.of(commentViewDto);
            Mockito.when(commentService.getCommentsByPostId(1L))
                    .thenReturn(comments);

            mockMvc.perform(get("/posts/{postId}/comments", 1L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(1L))
                    .andExpect(jsonPath("$[0].content").value("content"))
                    .andExpect(jsonPath("$[0].postId").value(1L));
        }

        @Test
        @DisplayName("Должен вернуть 404 Not Found при несуществующем postId")
        void givenNonExistentPostId_whenGetComments_thenReturnNotFound() throws Exception {
            Mockito.when(commentService.getCommentsByPostId(1L))
                    .thenThrow(new EntityNotFoundException("Post not found"));

            mockMvc.perform(get("/posts/{postId}/comments", 1L))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("DELETE /posts/{postId}/comments/{commentId} - Удаление комментария")
    class DeleteCommentTests {

        @Test
        @DisplayName("Должен успешно удалить комментарий и вернуть 204 No Content")
        void givenValidCommentId_whenDeleteComment_thenReturnNoContent() throws Exception {
            Mockito.doNothing().when(commentService).deleteComment(1L, 1L);

            mockMvc.perform(delete("/posts/{postId}/comments/{commentId}", 1L, 1L))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("Должен вернуть 404 Not Found при несуществующем commentId")
        void givenNonExistentCommentId_whenDeleteComment_thenReturnNotFound() throws Exception {
            Mockito.doThrow(new EntityNotFoundException("Comment not found"))
                    .when(commentService).deleteComment(1L, 999L);

            mockMvc.perform(delete("/posts/{postId}/comments/{commentId}", 1L, 999L))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Должен вернуть 404 Not Found при несуществующем postId")
        void givenNonExistentPostId_whenDeleteComment_thenReturnNotFound() throws Exception {
            Mockito.doThrow(new EntityNotFoundException("Post not found"))
                    .when(commentService).deleteComment(999L, 1L);

            mockMvc.perform(delete("/posts/{postId}/comments/{commentId}", 999L, 1L))
                    .andExpect(status().isNotFound());
        }
    }
}