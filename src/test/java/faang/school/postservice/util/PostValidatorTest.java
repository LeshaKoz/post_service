package faang.school.postservice.util;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostCreateDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validation.PostValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PostValidatorTest {

    private static final long VALID_AUTHOR_ID = 1L;
    private static final long VALID_PROJECT_ID = 1L;

    @Mock
    private PostRepository postRepository;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private ProjectServiceClient projectServiceClient;

    @InjectMocks
    private PostValidator postValidator;

    private PostCreateDto postCreateDto;
    private PostUpdateDto postUpdateDto;
    private UserDto userDto;
    private ProjectDto projectDto;

    @DisplayName("Проверка успешной валидации, когда передан valid PostCreateDto с authorId")
    @Test
    public void givenValidAuthorIdWhenValidateAuthorAndProjectThenSuccessfulValidation() {
        postCreateDto = new PostCreateDto();
        postCreateDto.setAuthorId(VALID_AUTHOR_ID);
        userDto = new UserDto(1L,"name","mail");

        Mockito.when(userServiceClient.getUser(postCreateDto.getAuthorId())).thenReturn(userDto);

        Assertions.assertDoesNotThrow(() ->
                postValidator.validateAuthorAndProject(postCreateDto));
    }

    @Test
    @DisplayName("Проверка успешной валидации, когда передан valid PostCreateDto с projectId")
    public void givenProjectIdWhenValidateAuthorAndProjectThenSuccessfulValidation() {
        PostCreateDto post = new PostCreateDto();
        post.setAuthorId(null);
        post.setProjectId(VALID_PROJECT_ID);

       projectDto = new ProjectDto(1L,"Test Project");

        Mockito.when(projectServiceClient.getProject(1L)).thenReturn(projectDto);

        Assertions.assertDoesNotThrow(() ->
            postValidator.validateAuthorAndProject(post));
    }

    @Test
    @DisplayName("Проверка получения ошибки при отсутствии authorId и projectId")
    public void givenInvalidAuthorIdAndProjectIdWhenValidateAuthorAndProjectThenDataValidationException() {
        PostCreateDto post = new PostCreateDto();
        post.setAuthorId(null);
        post.setProjectId(null);

        DataValidationException exception = Assertions.assertThrows(DataValidationException.class, () ->
            postValidator.validateAuthorAndProject(post));

        Assertions.assertEquals("Author or Project in the post is not found", exception.getMessage());
    }

    @Test
    @DisplayName("Проверка получения ошибки при несуществующем authorId")
    public void givenNonExistentAuthorIdWhenValidateAuthorAndProjectThenEntityNotFoundException() {
        PostCreateDto post = new PostCreateDto();
        post.setAuthorId(VALID_AUTHOR_ID);
        post.setProjectId(null);

        Mockito.when(userServiceClient.getUser(1L)).thenReturn(null);

        EntityNotFoundException exception = Assertions.assertThrows(EntityNotFoundException.class, () ->
            postValidator.validateAuthorAndProject(post));

        Assertions.assertEquals("Author does not exist", exception.getMessage());
    }

    @Test
    @DisplayName("Проверка получения ошибки при несуществующем projectId")
    public void givenNonExistentProjectIdWhenValidateAuthorAndProjectThenEntityNotFoundException() {
        PostCreateDto post = new PostCreateDto();
        post.setAuthorId(null);
        post.setProjectId(VALID_PROJECT_ID);

        Mockito.when(projectServiceClient.getProject(1L)).thenReturn(null);

        EntityNotFoundException exception = Assertions.assertThrows(EntityNotFoundException.class, () ->
            postValidator.validateAuthorAndProject(post));

        Assertions.assertEquals("Project does not exist", exception.getMessage());
    }

    @Test
    @DisplayName("Проверка успешной валидации, когда автор поста не изменился")
    public void givenValidPostUpdateDtoWithSameAuthorId_whenValidateAuthor_thenSuccessfulValidation() {
        long postId = 1L;
        Long authorId = 1L;

        Post oldPost = new Post();
        oldPost.setId(postId);
        oldPost.setAuthorId(authorId);

        postUpdateDto = new PostUpdateDto();
        postUpdateDto.setAuthorId(authorId);

        Mockito.when(postRepository.findById(postId)).thenReturn(java.util.Optional.of(oldPost));

        Assertions.assertDoesNotThrow(() ->
            postValidator.validateAuthor(postUpdateDto, postId));
    }

    @Test
    @DisplayName("Проверка получения ошибки, когда пост не найден")
    public void givenNonExistentPostId_whenValidateAuthor_thenThrowEntityNotFoundException() {
        long postId = 1L;
        PostUpdateDto postUpdateDto = new PostUpdateDto();
        postUpdateDto.setAuthorId(1L);

        Mockito.when(postRepository.findById(postId)).thenReturn(java.util.Optional.empty());

        EntityNotFoundException exception = Assertions.assertThrows(EntityNotFoundException.class, () ->
            postValidator.validateAuthor(postUpdateDto, postId));

        Assertions.assertEquals("Post not found with id: " + postId, exception.getMessage());
    }

    @Test
    @DisplayName("Проверка получения ошибки, когда автор поста изменился")
    public void givenPostUpdateDtoWithDifferentAuthorId_whenValidateAuthor_thenThrowDataValidationException() {
        long postId = 1L;
        Long oldAuthorId = 1L;
        Long newAuthorId = 2L;

        Post oldPost = new Post();
        oldPost.setId(postId);
        oldPost.setAuthorId(oldAuthorId);

        PostUpdateDto postUpdateDto = new PostUpdateDto();
        postUpdateDto.setAuthorId(newAuthorId);

        Mockito.when(postRepository.findById(postId)).thenReturn(java.util.Optional.of(oldPost));

        DataValidationException exception = Assertions.assertThrows(DataValidationException.class, () ->
            postValidator.validateAuthor(postUpdateDto, postId));

        Assertions.assertEquals("Authors should not differ", exception.getMessage());
    }
}
