package faang.school.postservice.util.controller;

import faang.school.postservice.controller.PostController;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.ExternalServiceValidationException;
import faang.school.postservice.service.PostService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostControllerTest {
    public static final Long TEST_USER_ID = 1L;
    public static final Long TEST_PROJECT_ID = 1L;
    public static final Long TEST_POST_ID = 1L;

    @Mock
    private PostService postService;

    @InjectMocks
    private PostController postController;

    @Test
    void createDraft_shouldReturnCreatedDraft() {
        PostDto inputPost = new PostDto();
        inputPost.setContent("Test content");
        inputPost.setAuthorId(1L);
        PostDto expectedPost = new PostDto();
        expectedPost.setId(1L);
        expectedPost.setContent("Test content");
        when(postService.createDraft(inputPost)).thenReturn(expectedPost);

        PostDto actualPost = postController.createDraft(inputPost);

        assertEquals(expectedPost, actualPost);
        verify(postService).createDraft(inputPost);
    }

    @Test
    void createDraft_WithNullContent_shouldThrowException() {
        PostDto inputPost = new PostDto();
        inputPost.setAuthorId(TEST_USER_ID);
        inputPost.setContent(null);

        doThrow(new ExternalServiceValidationException("Content cannot be null or empty"))
                .when(postService)
                .createDraft(inputPost);
        assertThrows(ExternalServiceValidationException.class, () -> postController.createDraft(inputPost));
        verify(postService, times(1)).createDraft(inputPost);
    }

    @Test
    void publish_shouldReturnPublishedPost() {
        PostDto expectedPost = new PostDto();
        expectedPost.setId(TEST_POST_ID);
        expectedPost.setPublished(true);
        when(postService.publish(TEST_POST_ID)).thenReturn(expectedPost);

        PostDto actualPost = postController.publish(TEST_POST_ID);

        assertEquals(expectedPost, actualPost);
        verify(postService).publish(TEST_POST_ID);
    }

    @Test
    void update_shouldReturnUpdatedPost() {
        String content = "Updated content";
        PostDto expectedPost = new PostDto();
        expectedPost.setId(TEST_POST_ID);
        expectedPost.setContent(content);
        when(postService.update(TEST_POST_ID, content)).thenReturn(expectedPost);

        PostDto actualPost = postController.update(TEST_POST_ID, content);

        assertEquals(expectedPost, actualPost);
        verify(postService).update(TEST_POST_ID, content);
    }

    @Test
    void softDelete_shouldReturnSoftDeletedPost() {
        PostDto expectedPost = new PostDto();
        expectedPost.setId(TEST_POST_ID);
        expectedPost.setDeleted(true);
        when(postService.softDelete(TEST_POST_ID)).thenReturn(expectedPost);

        PostDto actualPost = postController.softDelete(TEST_POST_ID);

        assertEquals(expectedPost, actualPost);
        verify(postService).softDelete(TEST_POST_ID);
    }

    @Test
    void getById_shouldReturnPostById() {
        PostDto expectedPost = new PostDto();
        expectedPost.setId(TEST_POST_ID);
        when(postService.getById(TEST_POST_ID)).thenReturn(expectedPost);

        PostDto actualPost = postController.getById(TEST_POST_ID);

        assertEquals(expectedPost, actualPost);
        verify(postService).getById(TEST_POST_ID);
    }

    @Test
    void getNotDeletedDraftByUserId_shouldReturnDraftsByUserId() {
        List<PostDto> expectedPosts = List.of(new PostDto(), new PostDto());
        when(postService.getNotDeletedDraftsByUserId(TEST_USER_ID)).thenReturn(expectedPosts);

        List<PostDto> actualPosts = postController.getNotDeletedDraftByUserId(TEST_USER_ID);

        assertEquals(expectedPosts, actualPosts);
        verify(postService).getNotDeletedDraftsByUserId(TEST_USER_ID);
    }

    @Test
    void getNotDeletedDraftsByProjectId_shouldReturnDraftsByProjectId() {
        List<PostDto> expectedPosts = List.of(new PostDto(), new PostDto());
        when(postService.getNotDeletedDraftsByProjectId(TEST_PROJECT_ID)).thenReturn(expectedPosts);

        List<PostDto> actualPosts = postController.getNotDeletedDraftsByProjectId(TEST_PROJECT_ID);

        assertEquals(expectedPosts, actualPosts);
        verify(postService).getNotDeletedDraftsByProjectId(TEST_PROJECT_ID);
    }

    @Test
    void getNotDeletedPublishedPostsByUserId_shouldReturnPublishedPostsByUserId() {
        List<PostDto> expectedPosts = List.of(new PostDto(), new PostDto());
        when(postService.getNotDeletedPublishedPostsByUserId(TEST_USER_ID)).thenReturn(expectedPosts);

        List<PostDto> actualPosts = postController.getNotDeletedPublishedPostsByUserId(TEST_USER_ID);

        assertEquals(expectedPosts, actualPosts);
        verify(postService).getNotDeletedPublishedPostsByUserId(TEST_USER_ID);
    }

    @Test
    void getNotDeletedPublishedPostsByProjectId_shouldReturnPublishedPostsByProjectId() {
        List<PostDto> expectedPosts = List.of(new PostDto(), new PostDto());
        when(postService.getNotDeletedPublishedPostsByProjectId(TEST_PROJECT_ID)).thenReturn(expectedPosts);

        List<PostDto> actualPosts = postController.getNotDeletedPublishedPostsByProjectId(TEST_PROJECT_ID);

        assertEquals(expectedPosts, actualPosts);
        verify(postService).getNotDeletedPublishedPostsByProjectId(TEST_PROJECT_ID);
    }
}
