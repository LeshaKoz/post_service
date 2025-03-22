package faang.school.postservice.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.controller.PostController;
import faang.school.postservice.dto.post.PostCreateDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.dto.post.PostViewDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.service.PostService;
import faang.school.postservice.validation.PostValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
public class PostControllerTest {

    private static final long VALID_AUTHOR_ID = 1L;
    private static final long INVALID_AUTHOR_ID = 999L;
    private static final long VALID_POST_ID = 1L;
    private static final long INVALID_POST_ID = 999L;
    private static final long VALID_PROJECT_ID = 1L;
    private static final long VALID_USER_ID = 1L;
    private static final String BAD_REQUEST_MESSAGE = "Ошибка валидации данных: ";
    private static final String NOT_FOUND_MESSAGE = "Сущность не найдена: ";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PostService postService;
    @MockBean
    private PostValidator postValidator;
    @MockBean
    private UserContext userContext;

    private Exception exception;
    private PostCreateDto postCreateDto;
    private PostViewDto postViewDto;
    private PostUpdateDto postUpdateDto;

    @DisplayName("Проверка создания черновика поста с валидными данными")
    @Test
    public void givenValidPostCreateDtoWhenCreateDraftThenReturnPostViewDto() throws Exception {
        postCreateDto = new PostCreateDto();
        postCreateDto.setContent("content");
        postCreateDto.setAuthorId(VALID_AUTHOR_ID);

        postViewDto = new PostViewDto();
        postViewDto.setContent("content");
        postViewDto.setAuthorId(VALID_AUTHOR_ID);

        Mockito.when(postService.createDraft(postCreateDto)).thenReturn(postViewDto);

        mockMvc.perform(post("/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postCreateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authorId").value(postCreateDto.getAuthorId()))
                .andExpect(jsonPath("$.content").value(postViewDto.getContent()));

        Mockito.verify(postValidator, Mockito.times(1)).validateAuthorAndProject(postCreateDto);
        Mockito.verify(postService, Mockito.times(1)).createDraft(postCreateDto);
    }

    @DisplayName("Проверка ошибки валидации при создании черновика с невалидными данными")
    @Test
    public void givenInvalidPostCreateDtoWhenCreateDraftThenReturnValidExceptions() throws Exception {
        postCreateDto = new PostCreateDto();

        mockMvc.perform(post("/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postCreateDto)))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("Проверка ошибки валидации при создании черновика с несуществующим автором или проектом")
    @Test
    public void givenInvalidAuthorAndProjectWhenCreateDraftThenReturnDataValidationException() throws Exception {
        postCreateDto = new PostCreateDto();
        postCreateDto.setContent("content");

        exception = new DataValidationException("Author or Project in the post is not found");
        Mockito.doThrow(exception)
                .when(postValidator).validateAuthorAndProject(postCreateDto);

        mockMvc.perform(post("/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postCreateDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(BAD_REQUEST_MESSAGE + exception.getMessage()));
    }

    @DisplayName("Проверка ошибки при создании черновика с несуществующим автором")
    @Test
    public void givenNotExistAuthorWhenCreateDraftThenReturnEntityNotFoundException() throws Exception {
        postCreateDto = new PostCreateDto();
        postCreateDto.setContent("content");
        postCreateDto.setAuthorId(INVALID_AUTHOR_ID);

        exception = new EntityNotFoundException("Author does not exist");
        Mockito.doThrow(exception)
                .when(postValidator).validateAuthorAndProject(postCreateDto);

        mockMvc.perform(post("/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postCreateDto)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(NOT_FOUND_MESSAGE + exception.getMessage()));
    }

    @DisplayName("Проверка ошибки при создании черновика с несуществующим проектом")
    @Test
    public void givenNotExistProjectWhenCreateDraftThenReturnEntityNotFoundException() throws Exception {
        postCreateDto = new PostCreateDto();
        postCreateDto.setContent("content");
        postCreateDto.setAuthorId(VALID_AUTHOR_ID);
        postCreateDto.setProjectId(INVALID_AUTHOR_ID);

        exception = new EntityNotFoundException("Project does not exist");
        Mockito.doThrow(exception)
                .when(postValidator).validateAuthorAndProject(postCreateDto);

        mockMvc.perform(post("/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postCreateDto)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(NOT_FOUND_MESSAGE + exception.getMessage()));
    }

    @DisplayName("Проверка публикации поста с валидным ID")
    @Test
    public void givenValidPostIdWhenPublishPostThenReturnPostViewDto() throws Exception {
        long postId = VALID_POST_ID;

        postViewDto = new PostViewDto();
        postViewDto.setContent("content");
        postViewDto.setAuthorId(VALID_AUTHOR_ID);

        Mockito.when(postService.publishPost(postId)).thenReturn(postViewDto);

        mockMvc.perform(put("/posts/{postId}/publish", postId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authorId").value(postViewDto.getAuthorId()))
                .andExpect(jsonPath("$.content").value(postViewDto.getContent()));

        Mockito.verify(postService, Mockito.times(1)).publishPost(postId);
    }

    @DisplayName("Проверка ошибки при публикации поста с несуществующим ID")
    @Test
    public void givenInvalidPostIdWhenPublishPostThenReturnEntityNotFoundException() throws Exception {
        long postId = INVALID_POST_ID;
        exception = new EntityNotFoundException("Post does not exist");
        Mockito.doThrow(exception)
                .when(postService).publishPost(postId);

        mockMvc.perform(put("/posts/{postId}/publish", postId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(NOT_FOUND_MESSAGE + exception.getMessage()));
    }

    @DisplayName("Проверка ошибки при публикации уже опубликованного поста")
    @Test
    public void givenPublishedPostWhenPublishPostThenReturnEntityDataValidationException() throws Exception {
        long postId = VALID_POST_ID;
        exception = new DataValidationException("Published post is not found");
        Mockito.doThrow(exception)
                .when(postService).publishPost(postId);

        mockMvc.perform(put("/posts/{postId}/publish", postId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(BAD_REQUEST_MESSAGE + exception.getMessage()));
    }

    @DisplayName("Проверка обновления поста с валидными данными")
    @Test
    public void givenValidPostUpdateDtoWhenUpdatePostThenReturnPostViewDto() throws Exception {
        long postId = VALID_POST_ID;
        postUpdateDto = new PostUpdateDto();
        postUpdateDto.setAuthorId(VALID_AUTHOR_ID);
        postUpdateDto.setContent("content");

        PostViewDto postViewDto = new PostViewDto();
        postViewDto.setContent("content");
        postViewDto.setAuthorId(VALID_AUTHOR_ID);

        Mockito.when(postService.updatePost(postUpdateDto, postId)).thenReturn(postViewDto);

        mockMvc.perform(put("/posts/{postId}/update", postId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postUpdateDto)))
                .andExpect(status().isOk());
    }

    @DisplayName("Проверка ошибки валидации при обновлении поста с невалидными данными")
    @Test
    public void givenInvalidPostUpdateDtoWhenUpdatePostThenReturnValidException() throws Exception {
        postUpdateDto = new PostUpdateDto();

        mockMvc.perform(put("/posts/{postId}/update", VALID_POST_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postUpdateDto)))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("Проверка ошибки при обновлении поста с несуществующим ID")
    @Test
    public void givenInvalidPostIdWhenUpdatePostThenReturnEntityNotFoundException() throws Exception {
        long postId = INVALID_POST_ID;
        postUpdateDto = new PostUpdateDto();
        postUpdateDto.setContent("content");
        postUpdateDto.setAuthorId(VALID_AUTHOR_ID);
        exception = new EntityNotFoundException("Post does not exist" + postId);
        Mockito.doThrow(exception)
                .when(postValidator).validateAuthor(postUpdateDto, postId);

        mockMvc.perform(put("/posts/{postId}/update", postId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postUpdateDto)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(NOT_FOUND_MESSAGE + exception.getMessage()));
    }

    @DisplayName("Проверка ошибки при обновлении поста с другим автором")
    @Test
    public void givenDifferentAuthorWhenUpdatePostThenReturnEntityDataValidationException() throws Exception {
        long postId = INVALID_POST_ID;
        postUpdateDto = new PostUpdateDto();
        postUpdateDto.setContent("content");
        postUpdateDto.setAuthorId(VALID_AUTHOR_ID);

        exception = new DataValidationException("Authors should not differ");
        Mockito.doThrow(exception)
                .when(postValidator).validateAuthor(postUpdateDto, postId);

        mockMvc.perform(put("/posts/{postId}/update", postId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postUpdateDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(BAD_REQUEST_MESSAGE + exception.getMessage()));
    }

    @DisplayName("Проверка мягкого удаления поста с валидным ID")
    @Test
    public void givenValidPostIdWhenSoftDeletePostThenReturnPostViewDto() throws Exception {
        long postId = VALID_POST_ID;
        postViewDto = new PostViewDto();
        postViewDto.setDeleted(true);

        Mockito.when(postService.softDeletePost(postId)).thenReturn(postViewDto);

        mockMvc.perform(put("/posts/{postId}/soft-delete", postId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @DisplayName("Проверка ошибки при мягком удалении поста с несуществующим ID")
    @Test
    public void givenInvalidPostIdWhenSoftDeletePostThenReturnEntityNotFoundException() throws Exception {
        long postId = INVALID_POST_ID;

        Mockito.doThrow(new EntityNotFoundException("Post not found with id: " + postId))
                .when(postService).softDeletePost(postId);

        mockMvc.perform(put("/posts/{postId}/soft-delete", postId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @DisplayName("Проверка получения поста с валидным ID")
    @Test
    public void givenValidPostIdWhenGetPostThenReturnPostViewDto() throws Exception {
        long postId = VALID_POST_ID;
        postViewDto = new PostViewDto();
        postViewDto.setAuthorId(VALID_AUTHOR_ID);
        Mockito.when(postService.getPost(postId)).thenReturn(postViewDto);

        mockMvc.perform(get("/posts/{postId}", postId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @DisplayName("Проверка ошибки при получении поста с несуществующим ID")
    @Test
    public void givenInvalidPostIdWhenGetPostThenReturnEntityNotFoundException() throws Exception {
        long postId = INVALID_POST_ID;

        exception = new EntityNotFoundException("Post not found with id: " + postId);
        Mockito.doThrow(exception)
                .when(postService).getPost(postId);

        mockMvc.perform(get("/posts/{postId}", postId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(NOT_FOUND_MESSAGE + exception.getMessage()));
    }

    @DisplayName("Проверка получения черновиков пользователя с валидным ID")
    @Test
    public void givenValidPostIdWhenGetUserDraftThenReturnListOfPostViewDto() throws Exception {
        long userId = VALID_USER_ID;

        Mockito.when(postService.getUserDraft(userId))
                .thenReturn(new ArrayList<>());

        mockMvc.perform(get("/posts/user/{userId}/draft", userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @DisplayName("Проверка получения черновиков проекта с валидным ID")
    @Test
    public void givenValidPostIdWhenGetProjectDraftThenReturnListOfPostViewDto() throws Exception {
        long projectId = VALID_PROJECT_ID;

        Mockito.when(postService.getUserDraft(projectId))
                .thenReturn(new ArrayList<>());

        mockMvc.perform(get("/posts/project/{projectId}/draft", projectId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @DisplayName("Проверка получения опубликованных постов автора с валидным ID")
    @Test
    public void givenValidPostIdWhenGetAuthorPublishedPostThenReturnListOfPostViewDto() throws Exception {
        long userId = VALID_USER_ID;

        Mockito.when(postService.getUserDraft(userId))
                .thenReturn(new ArrayList<>());

        mockMvc.perform(get("/posts/user/{userId}/published-post", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @DisplayName("Проверка получения опубликованных постов проекта с валидным ID")
    @Test
    public void givenValidPostIdWhenGetProjectPublishedPostThenReturnListOfPostViewDto() throws Exception {
        long projectId = VALID_PROJECT_ID;

        Mockito.when(postService.getUserDraft(projectId))
                .thenReturn(new ArrayList<>());

        mockMvc.perform(get("/posts/project/{projectId}/published-post", projectId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}