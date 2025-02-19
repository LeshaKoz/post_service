package faang.school.postservice.validator;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.filter.PostFilterDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Post;
import faang.school.postservice.validator.post.PostValidator;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostValidatorTest {

    private static final Long AUTHOR_ID = 1L;
    private static final Long PROJECT_ID = 2L;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private ProjectServiceClient projectServiceClient;

    @InjectMocks
    private PostValidator postValidator;

    private Post post;
    private UserDto userDto;
    private ProjectDto projectDto;

    @BeforeEach
    void setUp() {
        post = new Post();

        userDto = new UserDto(AUTHOR_ID, "testUser", "test@example.com");
        projectDto = new ProjectDto(AUTHOR_ID, "testProject");
    }

    @Test
    void validateNotPublished_Throws_WhenPublishedTest() {
        post.setPublishedAt(LocalDateTime.now());
        assertThrows(DataValidationException.class, () -> postValidator.validateNotPublished(post));
    }

    @Test
    void validateNotPublished_NotThrows_WhenNotPublishedTest() {
        post.setPublishedAt(null);
        assertDoesNotThrow(() -> postValidator.validateNotPublished(post));
    }

    @Test
    void validateNotDeleted_Throws_WhenPostIsDeletedTest() {
        post.setDeleted(true);
        assertThrows(DataValidationException.class, () -> postValidator.validateNotDeleted(post));
    }

    @Test
    void validateNotDeleted_NotThrows_WhenPostIsNotDeletedTest() {
        post.setDeleted(false);
        assertDoesNotThrow(() -> postValidator.validateNotDeleted(post));
    }

    @Test
    void validatePostAuthorExist_Throws_WhenAuthorDoesNotExistTest() {
        post.setAuthorId(AUTHOR_ID);
        when(userServiceClient.getUser(AUTHOR_ID)).thenThrow(FeignException.FeignClientException.class);
        assertThrows(DataValidationException.class, () -> postValidator.validatePostAuthorExist(post));
    }

    @Test
    void validatePostAuthorExist_NotThrows_WhenAuthorExistsTest() {
        post.setAuthorId(AUTHOR_ID);
        when(userServiceClient.getUser(AUTHOR_ID)).thenReturn(userDto);
        assertDoesNotThrow(() -> postValidator.validatePostAuthorExist(post));
    }

    @Test
    void validatePostAuthorExist_Throws_WhenProjectDoesNotExistTest() {
        post.setProjectId(PROJECT_ID);
        when(projectServiceClient.getProject(PROJECT_ID)).thenThrow(FeignException.FeignClientException.class);
        assertThrows(DataValidationException.class, () -> postValidator.validatePostAuthorExist(post));

    }

    @Test
    void validatePostAuthorExist_NotThrows_WhenProjectExistsTest() {
        post.setProjectId(PROJECT_ID);
        when(projectServiceClient.getProject(PROJECT_ID)).thenReturn(projectDto);
        assertDoesNotThrow(() -> postValidator.validatePostAuthorExist(post));
    }

    @Test
    void validatePostAuthorExist_Throws_WhenAuthorAndProjectExistsTest() {
        post.setAuthorId(null);
        post.setProjectId(null);

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> postValidator.validatePostAuthorExist(post));

        assertEquals("У поста должен быть либо автор, либо проект", exception.getMessage());
    }

    @Test
    void validateFilterDto_Throws_WhenProjectIdAndAuthorIdIsNull() {

        PostFilterDto postFilterDto = new PostFilterDto(null, null, true);
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> postValidator.validateFilterDto(postFilterDto));
        assertEquals("Необходимо указать authorId или projectId", exception.getMessage());
    }

    @Test
    void validateFilterDto_NotThrow_WhenOnlyAuthorIdIsSet() {

        PostFilterDto postFilterDto = new PostFilterDto(AUTHOR_ID, null, false);
        assertDoesNotThrow(() -> postValidator.validateFilterDto(postFilterDto));
    }

    @Test
    void validateFilterDto_NotThrow_WhenOnlyProjectIdIsSet() {

        PostFilterDto postFilterDto = new PostFilterDto(null, PROJECT_ID, false);
        assertDoesNotThrow(() -> postValidator.validateFilterDto(postFilterDto));
    }

    @Test
    void validateFilterDto_Throws_WhenProjectIdAndAuthorIdIsSet() {

        PostFilterDto postFilterDto = new PostFilterDto(AUTHOR_ID, PROJECT_ID, true);

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> postValidator.validateFilterDto(postFilterDto));

        assertEquals("Укажите либо authorId, либо projectId, но не оба одновременно", exception.getMessage());
    }
}
