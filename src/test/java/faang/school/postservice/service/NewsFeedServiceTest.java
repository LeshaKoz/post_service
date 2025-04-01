package faang.school.postservice.service;

import faang.school.postservice.config.props.CacheTtlProperties;
import faang.school.postservice.mapper.NewsFeedMapperImpl;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.cache.CacheAuthor;
import faang.school.postservice.model.cache.CacheComment;
import faang.school.postservice.model.cache.CachePost;
import faang.school.postservice.repository.cache.CacheAuthorRepository;
import faang.school.postservice.repository.cache.CachePostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.util.Optional;

import static faang.school.postservice.model.cache.CacheComment.COMMENT_PREFIX;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NewsFeedServiceTest {

    @Mock
    private CachePostRepository cachePostRepository;

    @Spy
    private NewsFeedMapperImpl newsFeedMapper = new NewsFeedMapperImpl();

    @Mock
    private CacheAuthorRepository cacheAuthorRepository;

    @Mock
    private UserService userService;

    @Spy
    private CacheTtlProperties cacheTTLProperties = new CacheTtlProperties(
            Duration.ofDays(1),
            Duration.ofDays(1)
    );

    @Mock
    private ProjectService projectService;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @InjectMocks
    private NewsFeedService newsFeedService;


    @Test
    void testCacheCommentForPostWithPostExist() {
        Post post = Post.builder().id(1L).build();
        Comment comment = Comment.builder()
                .post(post)
                .likesCount(0L)
                .content("content")
                .authorId(1L)
                .build();
        ListOperations<String, Object> listOperations = mock(ListOperations.class);

        when(redisTemplate.opsForList())
                .thenReturn(listOperations);
        when(cachePostRepository.existsById(anyLong()))
                .thenReturn(true);
        when(cacheAuthorRepository.findById(anyString()))
                .thenReturn(Optional.of(
                        CacheAuthor.builder()
                                .id("1")
                                .build()
                ));
        String cacheKey = COMMENT_PREFIX + comment.getPost().getId();

        newsFeedService.cacheCommentForPost(comment);

        ArgumentCaptor<CacheComment> captor = ArgumentCaptor.forClass(CacheComment.class);
        int maxComments = (Integer) ReflectionTestUtils.getField(newsFeedService, "maxComments");
        verify(listOperations).leftPush(eq(cacheKey), captor.capture());
        verify(listOperations).trim(
                cacheKey,
                0,
                maxComments - 1
        );
        CacheComment cacheComment = captor.getValue();
        assertEquals(comment.getContent(), cacheComment.getContent());
        assertEquals(comment.getLikesCount(), cacheComment.getLikes());

    }

    @Test
    void testCachePostWithPostExist() {
        Post post = Post.builder().id(1L).build();
        CachePost existingCachePost = CachePost.builder()
                .id(1L)
                .content("content")
                .build();

        when(cachePostRepository.findById(post.getId()))
                .thenReturn(Optional.of(existingCachePost));

        CachePost result = newsFeedService.cachePost(post);

        assertEquals(existingCachePost, result);
        verify(cachePostRepository, Mockito.never()).save(any(CachePost.class));
    }

    @Test
    void testCacheUserAsAuthor() {
        long userId = 1L;
        CacheAuthor expectedCacheAuthor = CacheAuthor.builder()
                .id("user_1")
                .userId(1L)
                .authorName("User Name")
                .timeToLeave(cacheTTLProperties.getAuthor().toMillis())
                .build();

        when(cacheAuthorRepository.findById("user_" + userId))
                .thenReturn(Optional.empty());
        when(userService.getUserDtoById(userId))
                .thenReturn(null); // Возвращаем null, так как нам важно только проверить, что метод вызывается
        when(cacheAuthorRepository.save(any(CacheAuthor.class)))
                .thenReturn(expectedCacheAuthor);

        CacheAuthor result = newsFeedService.cacheUserAsAuthor(userId);

        assertEquals(expectedCacheAuthor, result);
        verify(userService).getUserDtoById(userId);
        verify(cacheAuthorRepository).save(any(CacheAuthor.class));
    }

    @Test
    void testCacheProjectAsAuthor() {
        long projectId = 1L;
        CacheAuthor expectedCacheAuthor = CacheAuthor.builder()
                .id("project_1")
                .projectId(1L)
                .authorName("Project Name")
                .timeToLeave(cacheTTLProperties.getAuthor().toMillis())
                .build();

        when(cacheAuthorRepository.findById("project_" + projectId))
                .thenReturn(Optional.empty());
        when(projectService.getProjectById(projectId))
                .thenReturn(null); // Возвращаем null, так как нам важно только проверить, что метод вызывается
        when(cacheAuthorRepository.save(any(CacheAuthor.class)))
                .thenReturn(expectedCacheAuthor);

        CacheAuthor result = newsFeedService.cacheProjectAsAuthor(projectId);

        assertEquals(expectedCacheAuthor, result);
        verify(projectService).getProjectById(projectId);
        verify(cacheAuthorRepository).save(any(CacheAuthor.class));
    }

    @Test
    void testCachePostWithUserAuthor() {
        Post post = Post.builder()
                .id(1L)
                .authorId(2L)
                .content("post content")
                .build();
        CacheAuthor cacheAuthor = CacheAuthor.builder()
                .id("user_2")
                .authorName("User Name")
                .build();

        when(cachePostRepository.findById(post.getId()))
                .thenReturn(Optional.empty());
        when(cacheAuthorRepository.findById("user_" + post.getAuthorId()))
                .thenReturn(Optional.of(cacheAuthor));
        when(cachePostRepository.save(any(CachePost.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CachePost result = newsFeedService.cachePost(post);

        verify(cachePostRepository).save(any(CachePost.class));
        assertEquals("user_2", result.getAuthorId());
    }

    @Test
    void testCachePostWithProjectAuthor() {
        Post post = Post.builder()
                .id(1L)
                .projectId(3L)
                .content("project post")
                .build();
        CacheAuthor cacheAuthor = CacheAuthor.builder()
                .id("project_3")
                .authorName("Project Name")
                .build();

        when(cachePostRepository.findById(post.getId()))
                .thenReturn(Optional.empty());
        when(cacheAuthorRepository.findById("project_" + post.getProjectId()))
                .thenReturn(Optional.of(cacheAuthor));
        when(cachePostRepository.save(any(CachePost.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CachePost result = newsFeedService.cachePost(post);

        verify(cachePostRepository).save(any(CachePost.class));
        assertEquals("project_3", result.getAuthorId());
    }
}