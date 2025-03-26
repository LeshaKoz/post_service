package faang.school.postservice.util;

import faang.school.postservice.dto.post.PostCreateDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.dto.post.PostViewDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.PostService;
import faang.school.postservice.validation.PostValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    private static final long VALID_POST_ID = 1L;
    private static final long INVALID_POST_ID = 999L;
    private static final long VALID_PROJECT_ID = 1L;
    private static final long VALID_USER_ID = 1L;

    @Mock
    private PostRepository postRepository;
    @Mock
    private PostMapper postMapper;
    @Mock
    private PostValidator postValidator;

    @InjectMocks
    private PostService postService;

    private PostCreateDto postCreateDto;
    private PostViewDto postViewDto;
    private PostUpdateDto postUpdateDto;
    private Post post;

    @BeforeEach
    public void setUp() {
        postCreateDto = new PostCreateDto();
        postViewDto = new PostViewDto();
        postUpdateDto = new PostUpdateDto();
        post = new Post();
    }

    @Test
    @DisplayName("Проверка успешного создания черновика")
    public void givenPostCreateDtoWhenCreateDraftThenReturnPostViewDto() {
        Mockito.when(postMapper.createDtoToEntity(postCreateDto)).thenReturn(post);
        Mockito.when(postRepository.save(post)).thenReturn(post);
        Mockito.when(postMapper.toViewDto(post)).thenReturn(postViewDto);

        PostViewDto result = postService.createDraft(postCreateDto);

        Assertions.assertNotNull(result);

        Mockito.verify(postMapper, Mockito.times(1)).createDtoToEntity(postCreateDto);
        Mockito.verify(postRepository).save(post);
        Mockito.verify(postMapper).toViewDto(post);
    }

    @Test
    @DisplayName("Проверка успешной публикации поста")
    public void givenVaLidPostIdWhenPublishPostThenReturnPostViewDto() {
        long postId = VALID_POST_ID;
        post.setPublished(false);

        Mockito.when(postRepository.findById(postId))
                .thenReturn(Optional.of(post));
        Mockito.when(postMapper.toViewDto(post)).thenReturn(postViewDto);
        PostViewDto result = postService.publishPost(postId);

        Assertions.assertNotNull(result);

        Mockito.verify(postMapper, Mockito.times(1)).toViewDto(post);
        Mockito.verify(postRepository, Mockito.times(1)).findById(postId);
    }

    @Test
    @DisplayName("Проверка ошибки при публикации несуществующего поста")
    public void givenNotExistPostIdWhenPublishPostThenReturnEntityNotFoundException() {
        long postId = INVALID_POST_ID;

        Mockito.when(postRepository.findById(postId)).thenReturn(Optional.empty());
        Exception exception = Assertions.assertThrows(EntityNotFoundException.class,
                () -> postService.publishPost(postId));

        Assertions.assertEquals(String.format("Post not found with id: %s", postId), exception.getMessage());
    }

    @Test
    @DisplayName("Проверка ошибки при публикации уже опубликованного поста")
    public void givenPublishedPostIdWhenPublishPostThenReturnDataValidationException() {
        long postId = VALID_POST_ID;
        post.setPublished(true);

        Mockito.when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        Exception exception = Assertions.assertThrows(DataValidationException.class,
                () -> postService.publishPost(postId));

        Assertions.assertEquals("Post is already published", exception.getMessage());
    }

    @Test
    @DisplayName("Проверка успешного обновления поста")
    public void givenValidPostUpdateDtoAndPostIdWhenUpdatePostThenReturnPostViewDto() {
        long postId = VALID_POST_ID;

        Mockito.when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        Mockito.when(postMapper.toViewDto(post)).thenReturn(postViewDto);

        Assertions.assertNotNull(postService.updatePost(postUpdateDto, postId));

        Mockito.verify(postMapper, Mockito.times(1)).toViewDto(post);
        Mockito.verify(postRepository, Mockito.times(1)).findById(postId);
        Mockito.verify(postMapper, Mockito.times(1)).update(postUpdateDto, post);
    }

    @Test
    @DisplayName("Проверка ошибки при обновлении несуществующего поста")
    public void givenNotExistPostIdWhenUpdatePostThenReturnEntityNotFoundException() {
        long postId = INVALID_POST_ID;

        Mockito.when(postRepository.findById(postId)).thenReturn(Optional.empty());
        Exception exception = Assertions.assertThrows(EntityNotFoundException.class,
                () -> postService.updatePost(postUpdateDto, postId));

        Assertions.assertEquals(String.format("Post not found with id: %s", postId), exception.getMessage());
    }

    @Test
    @DisplayName("Проверка успешного мягкого удаления поста")
    public void givenValidPostIdWhenSoftDeletePostThenReturnPostViewDto() {
        long postId = VALID_POST_ID;
        post.setDeleted(false);

        Mockito.when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        Mockito.when(postMapper.toViewDto(post)).thenReturn(postViewDto);

        Assertions.assertNotNull(postService.softDeletePost(postId));

        Mockito.verify(postMapper, Mockito.times(1)).toViewDto(post);
        Mockito.verify(postRepository, Mockito.times(1)).findById(postId);
    }

    @Test
    @DisplayName("Проверка ошибки при мягком удалении несуществующего поста")
    public void givenNotExistPostIdWhenSoftDeletePostThenReturnEntityNotFoundException() {
        long postId = INVALID_POST_ID;

        Mockito.when(postRepository.findById(postId)).thenReturn(Optional.empty());

        Exception exception = Assertions.assertThrows(EntityNotFoundException.class,
                () -> postService.softDeletePost(postId));

        Assertions.assertEquals(String.format("Post not found with id: %s", postId), exception.getMessage());

        Mockito.verify(postRepository, Mockito.times(1)).findById(postId);
    }

    @Test
    @DisplayName("Проверка ошибки при мягком удалении уже удаленного поста")
    public void givenDeletedPostIdWhenSoftDeletePostThenReturnEntityDataValidationException() {
        long postId = VALID_POST_ID;
        post.setDeleted(true);

        Mockito.when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        Exception exception = Assertions.assertThrows(DataValidationException.class,
                () -> postService.softDeletePost(postId));

        Assertions.assertEquals(String.format("Post with id %s is already deleted", postId), exception.getMessage());

        Mockito.verify(postRepository, Mockito.times(1)).findById(postId);
    }

    @Test
    @DisplayName("Проверка успешного получения поста")
    public void givenValidPostIdWhenGetPostThenReturnPostViewDto() {
        long postId = VALID_POST_ID;

        Mockito.when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        Mockito.when(postMapper.toViewDto(post)).thenReturn(postViewDto);

        PostViewDto result = postService.getPost(postId);

        Assertions.assertNotNull(result);

        Mockito.verify(postMapper, Mockito.times(1)).toViewDto(post);
        Mockito.verify(postRepository, Mockito.times(1)).findById(postId);
    }

    @Test
    @DisplayName("Проверка ошибки при получении несуществующего поста")
    public void givenNotExistPostIdWhenGetPostThenReturnEntityNotFoundException() {
        long postId = INVALID_POST_ID;

        Mockito.when(postRepository.findById(postId)).thenReturn(Optional.empty());

        Exception exception = Assertions.assertThrows(EntityNotFoundException.class,
                () -> postService.getPost(postId));

        Assertions.assertEquals(String.format("Post not found with id: %s", postId), exception.getMessage());

        Mockito.verify(postRepository, Mockito.times(1)).findById(postId);
    }

    @Test
    @DisplayName("Проверка успешного получения черновиков пользователя")
    public void givenValidUserIdWhenGetUserDraftsThenReturnListOfPostViewDto() {
        long userId = VALID_USER_ID;
        post.setDeleted(false);
        post.setPublished(false);
        post.setCreatedAt(LocalDateTime.now());

        Mockito.when(postRepository.findByAuthorId(userId)).thenReturn(List.of(post));
        Mockito.when(postMapper.toViewDto(post)).thenReturn(postViewDto);

        Assertions.assertNotNull(postService.getUserDrafts(userId));

        Mockito.verify(postRepository, Mockito.times(1)).findByAuthorId(userId);
        Mockito.verify(postMapper, Mockito.times(1)).toViewDto(post);
    }

    @Test
    @DisplayName("Проверка получения пустого списка черновиков пользователя")
    public void givenInvalidUserIdWhenGetUserDraftsThenReturnListOfPostViewDto() {
        long userId = VALID_USER_ID;

        Mockito.when(postRepository.findByAuthorId(userId)).thenReturn(List.of());

        Assertions.assertNotNull(postService.getUserDrafts(userId));

        Mockito.verify(postRepository, Mockito.times(1)).findByAuthorId(userId);
    }

    @Test
    @DisplayName("Проверка успешного получения черновиков проекта")
    public void givenValidProjectIdWhenGetProjectDraftsThenReturnListOfPostViewDto() {
        long projectId = VALID_PROJECT_ID;
        post.setDeleted(false);
        post.setPublished(false);
        post.setCreatedAt(LocalDateTime.now());

        Mockito.when(postRepository.findByProjectId(projectId)).thenReturn(List.of(post));
        Mockito.when(postMapper.toViewDto(post)).thenReturn(postViewDto);

        Assertions.assertNotNull(postService.getProjectDrafts(projectId));

        Mockito.verify(postRepository, Mockito.times(1)).findByProjectId(projectId);
        Mockito.verify(postMapper, Mockito.times(1)).toViewDto(post);
    }

    @Test
    @DisplayName("Проверка получения пустого списка черновиков проекта")
    public void givenInvalidProjectIdWhenGetProjectDraftsThenReturnListOfPostViewDto() {
        long projectId = VALID_PROJECT_ID;

        Mockito.when(postRepository.findByProjectId(projectId)).thenReturn(List.of());

        Assertions.assertNotNull(postService.getProjectDrafts(projectId));

        Mockito.verify(postRepository, Mockito.times(1)).findByProjectId(projectId);
    }

    @Test
    @DisplayName("Проверка успешного получения опубликованных постов пользователя")
    public void givenValidUserIdWhenGetAuthorPublishedPostThenReturnListOfPostsViewDto() {
        long userId = VALID_USER_ID;
        post.setDeleted(false);
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());

        Mockito.when(postRepository.findByAuthorIdWithLikes(userId)).thenReturn(List.of(post));
        Mockito.when(postMapper.toViewDto(post)).thenReturn(postViewDto);

        Assertions.assertNotNull(postService.getAuthorPublishedPosts(userId));

        Mockito.verify(postRepository, Mockito.times(1))
                .findByAuthorIdWithLikes(userId);
        Mockito.verify(postMapper, Mockito.times(1))
                .toViewDto(post);
    }

    @Test
    @DisplayName("Проверка получения пустого списка опубликованных постов пользователя")
    public void givenInvalidUserIdWhenGetAuthorPublishedPostThenReturnListOfPostsViewDto() {
        long userId = VALID_USER_ID;
        post.setDeleted(false);
        post.setPublishedAt(LocalDateTime.now());

        Mockito.when(postRepository.findByAuthorIdWithLikes(userId)).thenReturn(List.of());

        Assertions.assertNotNull(postService.getAuthorPublishedPosts(userId));

        Mockito.verify(postRepository, Mockito.times(1)).findByAuthorIdWithLikes(userId);
    }

    @Test
    @DisplayName("Проверка успешного получения опубликованных постов проекта")
    public void givenValidProjectIdWhenGetProjectPublishedPostThenReturnListOfPostsViewDto() {
        long projectId = VALID_PROJECT_ID;
        post.setDeleted(false);
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());

        Mockito.when(postRepository.findByProjectIdWithLikes(projectId)).thenReturn(List.of(post));
        Mockito.when(postMapper.toViewDto(post)).thenReturn(postViewDto);

        Assertions.assertNotNull(postService.getProjectPublishedPosts(projectId));

        Mockito.verify(postRepository, Mockito.times(1)).findByProjectIdWithLikes(projectId);
        Mockito.verify(postMapper, Mockito.times(1)).toViewDto(post);
    }

    @Test
    @DisplayName("Проверка получения пустого списка опубликованных постов проекта")
    public void givenInvalidProjectIdWhenGetProjectPublishedPostThenReturnListOfPostsViewDto() {
        long projectId = VALID_PROJECT_ID;
        post.setDeleted(false);
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());

        Mockito.when(postRepository.findByProjectIdWithLikes(projectId)).thenReturn(List.of());

        Assertions.assertNotNull(postService.getProjectPublishedPosts(projectId));

        Mockito.verify(postRepository, Mockito.times(1)).findByProjectIdWithLikes(projectId);
    }
}
