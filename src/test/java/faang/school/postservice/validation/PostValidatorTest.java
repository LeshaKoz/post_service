package faang.school.postservice.validation;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostCreateDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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
    private static final long INVALID_AUTHOR_ID = 1L;
    private static final long INVALID_PROJECT_ID = 1L;

    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private ProjectServiceClient projectServiceClient;


    @InjectMocks
    private PostValidator postValidator;

    private PostCreateDto postCreateDto;
    private UserDto userDto;
    private ProjectDto projectDto;

    @BeforeEach
    public void setUp() {
        postCreateDto = new PostCreateDto();
    }

    @DisplayName("Проверка успешной валидации, когда передан valid PostCreateDto с authorId")
    @Test
    public void givenValidAuthorIdWhenValidateAuthorAndProjectThenSuccessfulValidation() {
        postCreateDto.setAuthorId(VALID_AUTHOR_ID);
        userDto = new UserDto(1L, "name", "mail");

        Mockito.when(userServiceClient.getUser(VALID_AUTHOR_ID)).thenReturn(userDto);

        Assertions.assertDoesNotThrow(() ->
                postValidator.validateAuthorAndProject(postCreateDto));
    }

    @Test
    @DisplayName("Проверка успешной валидации, когда передан valid PostCreateDto с projectId")
    public void givenProjectIdWhenValidateAuthorAndProjectThenSuccessfulValidation() {
        postCreateDto.setProjectId(VALID_PROJECT_ID);
        projectDto = new ProjectDto(1L, "Test Project");

        Mockito.when(projectServiceClient.getProject(VALID_PROJECT_ID)).thenReturn(projectDto);

        Assertions.assertDoesNotThrow(() ->
                postValidator.validateAuthorAndProject(postCreateDto));
    }

    @Test
    @DisplayName("Проверка получения ошибки при отсутствии authorId и projectId")
    public void givenInvalidAuthorIdAndProjectIdWhenValidateAuthorAndProjectThenDataValidationException() {
        DataValidationException exception = Assertions.assertThrows(DataValidationException.class,
                () -> postValidator.validateAuthorAndProject(postCreateDto));

        Assertions.assertEquals("Post cannot be missing an author and a project", exception.getMessage());
    }

    @Test
    @DisplayName("Проверка получения ошибки при несуществующем authorId")
    public void givenNonExistentAuthorIdWhenValidateAuthorAndProjectThenEntityNotFoundException() {
        postCreateDto.setAuthorId(INVALID_AUTHOR_ID);
        Mockito.when(userServiceClient.getUser(INVALID_AUTHOR_ID)).thenReturn(null);

        EntityNotFoundException exception = Assertions.assertThrows(EntityNotFoundException.class,
                () -> postValidator.validateAuthorAndProject(postCreateDto));

        Assertions.assertEquals("Author does not exist", exception.getMessage());
    }

    @Test
    @DisplayName("Проверка получения ошибки при несуществующем projectId")
    public void givenNonExistentProjectIdWhenValidateAuthorAndProjectThenEntityNotFoundException() {
        postCreateDto.setProjectId(INVALID_PROJECT_ID);
        Mockito.when(projectServiceClient.getProject(INVALID_PROJECT_ID)).thenReturn(null);

        EntityNotFoundException exception = Assertions.assertThrows(EntityNotFoundException.class,
                () -> postValidator.validateAuthorAndProject(postCreateDto));

        Assertions.assertEquals("Project does not exist", exception.getMessage());
    }
}
