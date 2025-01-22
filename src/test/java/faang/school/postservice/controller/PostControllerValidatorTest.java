package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostRequestDto;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PostControllerValidatorTest {
    @InjectMocks
    PostControllerValidator postControllerValidator;
    PostRequestDto validPostRequestDto;
    PostRequestDto notValidPostRequestDto1;
    PostRequestDto notValidPostRequestDto2;
    @BeforeEach
    void setUp() {
        validPostRequestDto = PostRequestDto.builder()
                .id(1L)
                .content("test content")
                .authorId(111L)
                .projectId(222L)
                .build();
        notValidPostRequestDto1 = PostRequestDto.builder()
                .id(1L)
                .authorId(111L)
                .projectId(222L)
                .build();
        notValidPostRequestDto2 = PostRequestDto.builder()
                .content("test content")
                .authorId(111L)
                .projectId(222L)
                .build();
    }

    @Test
    @DisplayName("Test post id")
    void testValidatePostId() {
        Assert.assertThrows(IllegalArgumentException.class,
                () -> postControllerValidator.validatePostId(null));
        Assert.assertThrows(IllegalArgumentException.class,
                () -> postControllerValidator.validatePostId(0L));
        Assert.assertThrows(IllegalArgumentException.class,
                () -> postControllerValidator.validatePostId(-1L));
        postControllerValidator.validatePostId(100L);
    }

    @Test
    @DisplayName("Test user id")
    void testValidateUserId() {
        Assert.assertThrows(IllegalArgumentException.class,
                () -> postControllerValidator.validateUserId(null));
        Assert.assertThrows(IllegalArgumentException.class,
                () -> postControllerValidator.validateUserId(0L));
        Assert.assertThrows(IllegalArgumentException.class,
                () -> postControllerValidator.validateUserId(-1L));
        postControllerValidator.validateUserId(100L);
    }

    @Test
    @DisplayName("Test project id")
    void validateProjectId() {
        Assert.assertThrows(IllegalArgumentException.class,
                () -> postControllerValidator.validateProjectId(null));
        Assert.assertThrows(IllegalArgumentException.class,
                () -> postControllerValidator.validateProjectId(0L));
        Assert.assertThrows(IllegalArgumentException.class,
                () -> postControllerValidator.validateProjectId(-1L));
        postControllerValidator.validateProjectId(100L);
    }

    @Test
    void testValidateDto() {
        Assert.assertThrows(IllegalArgumentException.class,
                () -> postControllerValidator.validateDto(notValidPostRequestDto1));
        Assert.assertThrows(IllegalArgumentException.class,
                () -> postControllerValidator.validateDto(notValidPostRequestDto2));
        postControllerValidator.validateDto(validPostRequestDto);
    }
}