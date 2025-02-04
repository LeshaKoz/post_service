package faang.school.postservice.validator;

import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

class PostValidatorTest {
    private UserServiceClient userServiceClient;
    private ProjectServiceClient projectServiceClient;
    private PostValidator postValidator;

    @BeforeEach
    public void setUp() {
        userServiceClient = mock(UserServiceClient.class);
        projectServiceClient = mock(ProjectServiceClient.class);
        postValidator = new PostValidator(userServiceClient, projectServiceClient);
    }

    @Test
    public void validatePostDraftInfo_ThrowsWhenContentIsBlank() {
        Post post = new Post();
        post.setContent("");

        assertThrows(DataValidationException.class, () -> postValidator.validatePostDraftInfo(post));
    }

    @Test
    public void validatePostDraftInfo_ThrowsWhenNoAuthor() {
        Post post = new Post();
        post.setContent("Content");

        assertThrows(DataValidationException.class, () -> postValidator.validatePostDraftInfo(post));
    }

    @Test
    public void validatePostDraftInfo_ThrowsWhenBothAuthors() {
        Post post = new Post();
        post.setContent("Content");
        post.setAuthorId(1L);
        post.setProjectId(1L);

        assertThrows(DataValidationException.class, () -> postValidator.validatePostDraftInfo(post));
    }

    @Test
    public void validatePostDraftInfo_ShouldNotThrowWithValidContentAndOneAuthor() {
        Post post = new Post();
        post.setContent("Content");
        post.setAuthorId(1L);

        assertDoesNotThrow(() -> postValidator.validatePostDraftInfo(post));
    }

    @Test
    public void validatePostAuthorExist_NotThrowsWhenUserExist() {
        Post post = new Post();
        post.setAuthorId(1L);
        when(userServiceClient.getUser(1L)).thenReturn(new UserDto(1L, "name", "email"));

        assertDoesNotThrow(() -> postValidator.validatePostAuthorExist(post));

        verify(userServiceClient, times(1)).getUser(1L);
        verify(projectServiceClient, never()).getProject(anyLong());
    }

    @Test
    public void validatePostAuthorExist_NotThrowsWhenProjectExist() {
        Post post = new Post();
        post.setProjectId(1L);
        when(projectServiceClient.getProject(1L)).thenReturn(new ProjectDto(1L, "title"));

        assertDoesNotThrow(() -> postValidator.validatePostAuthorExist(post));

        verify(projectServiceClient, times(1)).getProject(1L);
        verify(userServiceClient, never()).getUser(anyLong());
    }

    @Test
    public void validatePostAuthorExist_ThrowsWhenAuthorNotExist() {
        Post post = new Post();
        post.setAuthorId(1L);
        when(userServiceClient.getUser(1L)).thenReturn(null);

        assertThrows(DataValidationException.class, () -> postValidator.validatePostAuthorExist(post));
    }

    @Test
    public void validateNotPublished_ThrowsWhenPostPublished() {
        Post post = new Post();
        post.setPublished(true);

        assertThrows(DataValidationException.class, () -> postValidator.validateNotPublished(post));
    }

    @Test
    public void validateNotPublished_ShouldNotThrowWhenNotYetPublished() {
        Post post = new Post();
        post.setPublished(false);

        assertDoesNotThrow(() -> postValidator.validateNotPublished(post));
    }

    @Test
    public void validateNotDeleted_ThrowsWhenPostDeleted() {
        Post post = new Post();
        post.setDeleted(true);

        assertThrows(DataValidationException.class, () -> postValidator.validateNotDeleted(post));
    }

    @Test
    public void validateNotDeleted_ShouldNotThrowWhenNotDeleted() {
        Post post = new Post();
        post.setDeleted(false);

        assertDoesNotThrow(() -> postValidator.validateNotDeleted(post));
    }
}
