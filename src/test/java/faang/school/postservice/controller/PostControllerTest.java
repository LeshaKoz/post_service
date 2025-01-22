package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostRequestDto;
import faang.school.postservice.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PostControllerTest {

    @Mock
    PostService postService;
    @InjectMocks
    PostController postController;
    PostRequestDto validPostRequestDto;
    PostRequestDto validUpdatedPostRequestDto;
    @BeforeEach
    void setUp() {
        validPostRequestDto = PostRequestDto.builder()
                .id(1L)
                .content("test content")
                .authorId(111L)
                .projectId(222L)
                .build();
        validUpdatedPostRequestDto = PostRequestDto.builder()
                .id(1L)
                .content("test content updated")
                .authorId(111L)
                .projectId(222L)
                .build();
    }

    @Test
    @DisplayName("Test create draft")
    void testCreatePostDraftByValidDto() {
        postController.createPostDraft(validPostRequestDto);
        Mockito.verify(postService, Mockito.times(1))
                .createPostDraft(validPostRequestDto);
    }

    @Test
    @DisplayName("Test publish post")
    void testPublishPostDraft() {
        Long postId = 123L;
        postController.publishPostDraft(postId);
        Mockito.verify(postService, Mockito.times(1))
                .publishPostDraft(postId);
    }

    @Test
    @DisplayName("Test update post")
    void testUpdatePost() {
        postController.updatePost(validUpdatedPostRequestDto);
        Mockito.verify(postService, Mockito.times(1))
                .updatePost(validUpdatedPostRequestDto);
    }

    @Test
    @DisplayName("Test delete post")
    void testDeletePost() {
        Long postId = 123L;
        postController.deletePost(postId);
        Mockito.verify(postService, Mockito.times(1))
                .deletePost(postId);
    }

    @Test
    @DisplayName("Test get post")
    void testGetPost() {
        Long postId = 123L;
        postController.getPost(postId);
        Mockito.verify(postService, Mockito.times(1))
                .getPost(postId);
    }

    @Test
    @DisplayName("Test get project's post drafts")
    void testGetProjectPostDrafts() {
        Long projectId = 123L;
        postController.getProjectPostDrafts(projectId);
        Mockito.verify(postService, Mockito.times(1))
                .getProjectPostDrafts(projectId);
    }

    @Test
    @DisplayName("Test get user's post drafts")
    void testGetUserPostDrafts() {
        Long userId = 123L;
        postController.getUserPostDrafts(userId);
        Mockito.verify(postService, Mockito.times(1))
                .getUserPostDrafts(userId);
    }

    @Test
    @DisplayName("Test get project's posts")
    void testGetProjectPosts() {
        Long projectId = 123L;
        postController.getProjectPosts(projectId);
        Mockito.verify(postService, Mockito.times(1))
                .getProjectPosts(projectId);
    }

    @Test
    @DisplayName("Test get user's posts")
    void testGetUserPosts() {
        Long userId = 123L;
        postController.getUserPosts(userId);
        Mockito.verify(postService, Mockito.times(1))
                .getUserPosts(userId);
    }
}