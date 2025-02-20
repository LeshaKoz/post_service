package faang.school.postservice.util.service;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.ExternalServiceValidationException;
import faang.school.postservice.exception.PostNotFoundException;
import faang.school.postservice.gateway.ProjectServiceGateway;
import faang.school.postservice.gateway.UserServiceGateway;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.AIService;
import faang.school.postservice.service.impl.PostServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static faang.school.postservice.service.impl.PostServiceImpl.POSTS_MUST_HAVE_ONE_AUTHOR;
import static faang.school.postservice.service.impl.PostServiceImpl.POST_WITH_ID_ALREADY_PUBLISHED;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {
    private static final Long TEST_USER_ID = 1L;
    private static final Long TEST_PROJECT_ID = 1L;
    private static final Long TEST_POST_ID = 1L;
    @Mock
    private PostRepository postRepository;
    @Mock
    private UserServiceGateway userServiceGateway;
    @Mock
    private ProjectServiceGateway projectServiceGateway;
    @Mock
    private PostMapper postMapper;
    @Mock
    private AIService aiService;
    @InjectMocks
    private PostServiceImpl postService;

    @Test
    void createDraft_shouldReturnCreatedDraft() {
        PostDto inputPost = new PostDto();
        inputPost.setAuthorId(TEST_USER_ID);
        inputPost.setContent("Test content");
        Post postEntity = new Post();
        PostDto expectedPost = new PostDto();
        expectedPost.setId(TEST_POST_ID);

        when(postMapper.toEntity(inputPost)).thenReturn(postEntity);
        when(postRepository.save(postEntity)).thenReturn(postEntity);
        when(postMapper.toDto(postEntity)).thenReturn(expectedPost);

        PostDto actualPost = postService.createDraft(inputPost);

        assertEquals(expectedPost, actualPost);
        verify(userServiceGateway).getUser(TEST_USER_ID);
        verify(postMapper).toEntity(inputPost);
        verify(postRepository).save(postEntity);
        verify(postMapper).toDto(postEntity);
    }

    @Test
    void createDraft_shouldThrowExceptionForInvalidInput() {
        PostDto invalidPost = new PostDto();

        ExternalServiceValidationException exception = assertThrows(
                ExternalServiceValidationException.class,
                () -> postService.createDraft(invalidPost)
        );

        assertEquals(POSTS_MUST_HAVE_ONE_AUTHOR, exception.getMessage());
        verifyNoInteractions(postRepository, postMapper, userServiceGateway, projectServiceGateway);
    }


    @Test
    void publish_shouldReturnPublishedPost() {
        Post post = new Post();
        post.setPublished(false);
        PostDto expectedPost = new PostDto();

        when(postRepository.findById(TEST_POST_ID)).thenReturn(Optional.of(post));
        when(postRepository.save(post)).thenReturn(post);
        when(postMapper.toDto(post)).thenReturn(expectedPost);

        PostDto actualPost = postService.publish(TEST_POST_ID);

        assertEquals(expectedPost, actualPost);
        assertTrue(post.isPublished());
        verify(postRepository).findById(TEST_POST_ID);
        verify(postRepository).save(post);
        verify(postMapper).toDto(post);
    }

    @Test
    void publish_shouldThrowExceptionForAlreadyPublishedPost() {
        Post post = new Post();
        post.setPublished(true);

        when(postRepository.findById(TEST_POST_ID)).thenReturn(Optional.of(post));

        ExternalServiceValidationException exception = assertThrows(
                ExternalServiceValidationException.class,
                () -> postService.publish(TEST_POST_ID)
        );

        assertEquals(String.format(POST_WITH_ID_ALREADY_PUBLISHED, TEST_POST_ID), exception.getMessage());
        verify(postRepository).findById(TEST_POST_ID);
        verifyNoMoreInteractions(postRepository);
    }

    @Test
    void update_shouldReturnUpdatedPost() {
        Post post = new Post();
        String updatedContent = "Updated content";
        PostDto expectedPost = new PostDto();

        when(postRepository.findById(TEST_POST_ID)).thenReturn(Optional.of(post));
        when(postRepository.save(post)).thenReturn(post);
        when(postMapper.toDto(post)).thenReturn(expectedPost);

        PostDto actualPost = postService.update(TEST_POST_ID, updatedContent);

        assertEquals(expectedPost, actualPost);
        assertEquals(updatedContent, post.getContent());
        verify(postRepository).findById(TEST_POST_ID);
        verify(postRepository).save(post);
        verify(postMapper).toDto(post);
    }

    @Test
    void softDelete_shouldReturnSoftDeletedPost() {
        Post post = new Post();
        PostDto expectedPost = new PostDto();

        when(postRepository.findById(TEST_POST_ID)).thenReturn(Optional.of(post));
        when(postRepository.save(post)).thenReturn(post);
        when(postMapper.toDto(post)).thenReturn(expectedPost);

        PostDto actualPost = postService.softDelete(TEST_POST_ID);

        assertEquals(expectedPost, actualPost);
        assertTrue(post.isDeleted());
        verify(postRepository).findById(TEST_POST_ID);
        verify(postRepository).save(post);
        verify(postMapper).toDto(post);
    }

    @Test
    void getById_shouldReturnPostById() {
        Post post = new Post();
        PostDto expectedPost = new PostDto();

        when(postRepository.findById(TEST_POST_ID)).thenReturn(Optional.of(post));
        when(postMapper.toDto(post)).thenReturn(expectedPost);

        PostDto actualPost = postService.getById(TEST_POST_ID);

        assertEquals(expectedPost, actualPost);
        verify(postRepository).findById(TEST_POST_ID);
        verify(postMapper).toDto(post);
    }

    @Test
    void getNotDeletedDraftsByUserId_shouldReturnDrafts() {
        Post post1 = new Post();
        post1.setCreatedAt(LocalDateTime.now());
        Post post2 = new Post();
        post2.setCreatedAt(LocalDateTime.now());
        List<Post> posts = List.of(post1, post2);
        List<PostDto> expectedPosts = List.of(new PostDto(), new PostDto());

        when(postRepository.findByAuthorId(TEST_USER_ID)).thenReturn(posts);
        when(postMapper.toDto(any(Post.class))).thenReturn(expectedPosts.get(0), expectedPosts.get(1));

        List<PostDto> actualPosts = postService.getNotDeletedDraftsByUserId(TEST_USER_ID);

        assertEquals(expectedPosts, actualPosts);
        verify(postRepository).findByAuthorId(TEST_USER_ID);
        verify(postMapper, times(2)).toDto(any(Post.class));
    }

    @Test
    void getNotDeletedDraftsByProjectId_shouldReturnDrafts() {
        Post post1 = new Post();
        post1.setCreatedAt(LocalDateTime.now());
        Post post2 = new Post();
        post2.setCreatedAt(LocalDateTime.now());
        List<Post> posts = List.of(post1, post2);
        List<PostDto> expectedPosts = List.of(new PostDto(), new PostDto());

        when(postRepository.findByProjectId(TEST_PROJECT_ID)).thenReturn(posts);
        when(postMapper.toDto(any(Post.class))).thenReturn(expectedPosts.get(0), expectedPosts.get(1));

        List<PostDto> actualPosts = postService.getNotDeletedDraftsByProjectId(TEST_PROJECT_ID);

        assertEquals(expectedPosts, actualPosts);
        verify(postRepository).findByProjectId(TEST_PROJECT_ID);
        verify(postMapper, times(2)).toDto(any(Post.class));
    }

    @Test
    void getNotDeletedPublishedPostsByUserId_shouldReturnDrafts() {
        Post post1 = new Post();
        post1.setCreatedAt(LocalDateTime.now());
        post1.setDeleted(false);
        post1.setPublished(true);
        Post post2 = new Post();
        post2.setCreatedAt(LocalDateTime.now()
                .plusMinutes(1));
        post2.setDeleted(false);
        post2.setPublished(true);
        List<Post> posts = List.of(post1, post2);
        List<PostDto> expectedPosts = List.of(new PostDto(), new PostDto());

        when(postRepository.findByAuthorId(TEST_USER_ID)).thenReturn(posts);
        when(postMapper.toDto(any(Post.class))).thenReturn(expectedPosts.get(0), expectedPosts.get(1));

        List<PostDto> actualPosts = postService.getNotDeletedPublishedPostsByUserId(TEST_USER_ID);

        assertEquals(expectedPosts, actualPosts);
        verify(postRepository).findByAuthorId(TEST_USER_ID);
        verify(postMapper, times(2)).toDto(any(Post.class));
    }

    @Test
    void getNotDeletedPublishedPostsByProjectId_shouldReturnDrafts() {
        Post post1 = new Post();
        post1.setCreatedAt(LocalDateTime.now());
        post1.setDeleted(false);
        post1.setPublished(true);
        Post post2 = new Post();
        post2.setCreatedAt(LocalDateTime.now()
                .plusMinutes(1));
        post2.setDeleted(false);
        post2.setPublished(true);
        List<Post> posts = List.of(post1, post2);
        List<PostDto> expectedPosts = List.of(new PostDto(), new PostDto());

        when(postRepository.findByProjectId(TEST_PROJECT_ID)).thenReturn(posts);
        when(postMapper.toDto(any(Post.class))).thenReturn(expectedPosts.get(0), expectedPosts.get(1));

        List<PostDto> actualPosts = postService.getNotDeletedPublishedPostsByProjectId(TEST_PROJECT_ID);

        assertEquals(expectedPosts, actualPosts);
        verify(postRepository).findByProjectId(TEST_PROJECT_ID);
        verify(postMapper, times(2)).toDto(any(Post.class));
    }

    @Test
    void getNotDeletedPublishedPostsByProjectId_WithNonExistingProjectId_shouldThrowException() {
        when(postRepository.findByProjectId(TEST_PROJECT_ID)).thenReturn(Collections.emptyList());

        assertThrows(PostNotFoundException.class, () ->
                postService.getNotDeletedPublishedPostsByProjectId(TEST_PROJECT_ID));
    }

    @Test
    void grammarCorrectionPost_shouldCorrectGrammarAndSavePost() {
        Post post = new Post();
        post.setId(TEST_POST_ID);
        post.setContent("Original content");

        when(aiService.checkGrammarPost(post)).thenReturn("Corrected content");

        postService.grammarCorrectionPost(post);

        assertEquals("Corrected content", post.getContent());
        assertTrue(post.isAiChecked());
        verify(postRepository).save(post);
        verify(aiService).checkGrammarPost(post);
    }

    @Test
    void grammarCorrectionPost_shouldLogErrorWhenExceptionOccurs() {
        Post post = new Post();
        post.setId(TEST_POST_ID);
        post.setContent("Original content");

        doThrow(new RuntimeException("AI service error")).when(aiService).checkGrammarPost(post);

        postService.grammarCorrectionPost(post);

        assertEquals("Original content", post.getContent());
        assertFalse(post.isAiChecked());
        verify(postRepository, never()).save(post);
        verify(aiService).checkGrammarPost(post);
    }
}
