package faang.school.postservice.service.impl;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostRequestDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Post;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PostServiceValidatorTest {

    @InjectMocks
    PostServiceValidator postServiceValidator;
    @Mock
    ProjectServiceClient projectServiceClient;
    @Mock
    UserServiceClient userServiceClient;


    PostRequestDto validPostRequestDto;
    UserDto userDto;
    ProjectDto projectDto;
    Post post;

    @BeforeEach
    void setUp() {
        validPostRequestDto = PostRequestDto.builder()
                .id(123L)
                .content("test content")
                .authorId(111L)
                .projectId(222L)
                .build();
        post = Post.builder()
                .id(123L)
                .content("some content")
                .authorId(111L)
                .build();
        userDto = new UserDto(111L, "Alice", "alice@mail.ru");
        projectDto = new ProjectDto(222L, "New project");
    }

    @Test
    @DisplayName("Test authorship for post")
    void testValidateAuthorshipPostDto() {
        PostRequestDto emptyAuthorsPostDto = PostRequestDto.builder()
                .id(123L)
                .content("test")
                .build();
        Assert.assertThrows(IllegalArgumentException.class,
                () -> postServiceValidator.validatePostDto(emptyAuthorsPostDto));

        PostRequestDto emptyAuthorIdIsZeroPostDto = PostRequestDto.builder()
                .id(123L)
                .authorId(0L)
                .content("test")
                .build();
        Assert.assertThrows(IllegalArgumentException.class,
                () -> postServiceValidator.validatePostDto(emptyAuthorIdIsZeroPostDto));

        PostRequestDto emptyProjectIdIsZeroPostDto = PostRequestDto.builder()
                .id(123L)
                .projectId(0L)
                .content("test")
                .build();
        Assert.assertThrows(IllegalArgumentException.class,
                () -> postServiceValidator.validatePostDto(emptyProjectIdIsZeroPostDto));

        Mockito.when(userServiceClient.getUser(111L)).thenReturn(userDto);
        Mockito.when(projectServiceClient.getProject(222L)).thenReturn(projectDto);

        postServiceValidator.validatePostDto(validPostRequestDto);
    }

    @Test
    @DisplayName("Test author and project exist for post")
    void testValidateAuthorAndProjectPostDto() {
        Mockito.when(userServiceClient.getUser(11111111L)).thenReturn(new UserDto(null, null, null));
        Mockito.when(projectServiceClient.getProject(222222222L)).thenReturn(new ProjectDto(0, null));
        PostRequestDto unknownAuthorPostDto = PostRequestDto.builder()
                .id(123L)
                .authorId(11111111L)
                .content("test")
                .build();

        Assert.assertThrows(IllegalArgumentException.class,
                () -> postServiceValidator.validatePostDto(unknownAuthorPostDto));

        PostRequestDto unknownProjectPostDto = PostRequestDto.builder()
                .id(123L)
                .projectId(222222222L)
                .content("test")
                .build();
        Assert.assertThrows(IllegalArgumentException.class,
                () -> postServiceValidator.validatePostDto(unknownProjectPostDto));
    }

    @Test
    @DisplayName("Test post exists")
    void testValidatePostExists() {
        Post post = Post.builder()
                .content("some content")
                .authorId(222L)
                .build();
        Assert.assertThrows(IllegalArgumentException.class,
                () -> postServiceValidator.validatePostExists(123L, post));
    }

    @Test
    void testValidatePostBeforePublish() {
        Post post = Post.builder()
                .id(123L)
                .content("some content")
                .authorId(222L)
                .published(true)
                .build();
        Assert.assertThrows(IllegalArgumentException.class,
                () -> postServiceValidator.validatePostBeforePublish(post));
    }

    @Test
    void testValidatePostBeforeUpdate() {
        //TODO написать тест
        postServiceValidator.validatePostBeforeUpdate(post, post);
    }
}