package faang.school.postservice.service.feed;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import faang.school.postservice.dto.feed.FeedResponseDto;
import faang.school.postservice.dto.post.PostRedisDto;
import faang.school.postservice.dto.user.UserRedisDto;
import faang.school.postservice.mapper.FeedMapper;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.redis.RedisFeedRepository;
import faang.school.postservice.repository.redis.RedisPostRepository;
import faang.school.postservice.repository.redis.RedisUserRepository;
import faang.school.postservice.service.news_feed.FeedService;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.LongStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class FeedServiceTest {
    @Mock
    private RedisFeedRepository redisFeedRepository;

    @Mock
    private RedisUserRepository redisUserRepository;

    @Mock
    private RedisPostRepository redisPostRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostMapper postMapper;

    @Mock
    private FeedMapper feedMapper;

    @InjectMocks
    private FeedService feedService;

    private final Long USER_ID = 1L;
    private final int PAGE_SIZE = 20;

//    @Test
//    void getUserFeed_WhenUserNotExists_ShouldThrowException() {
//        when(redisUserRepository.checkUserExist(USER_ID)).thenReturn(true);
//
//        assertThrows(EntityNotFoundException.class,
//            () -> feedService.getUserFeed(USER_ID, 0L),
//            "Expected exception when user not found");
//    }
//
//    @Test
//    void getUserFeed_WhenAllPostsInRedis_ShouldReturnFeed() {
//        List<Long> postIds = LongStream.rangeClosed(1, PAGE_SIZE)
//            .boxed()
//            .toList();
//
//        List<PostRedisDto> redisPosts = createRedisPosts(postIds);
//        List<UserRedisDto> users = createUsers(List.of(USER_ID));
//
//        when(redisUserRepository.checkUserExist(USER_ID)).thenReturn(false);
////        when(redisFeedRepository.getFeedPostIds(USER_ID, 0L, PAGE_SIZE)).thenReturn(postIds);
//        when(redisPostRepository.getPosts(postIds)).thenReturn(redisPosts);
//        when(redisUserRepository.getUsers(anyList())).thenReturn(users);
//        when(feedMapper.toFeedResponseDto(any(), any()))
//            .thenReturn(new FeedResponseDto("shuler", "asfsf", LocalDateTime.now()));
//
//        List<FeedResponseDto> result = feedService.getUserFeed(USER_ID, 0L);
//
//        assertEquals(PAGE_SIZE, result.size());
//        verify(redisPostRepository, never()).cachePosts(anyList());
//        verify(postRepository, never()).findAllById(anyList());
//    }
//
//    @Test
//    void getUserFeed_WhenSomePostsMissing_ShouldFetchFromDatabase() {
//        List<Long> feedPostIds = List.of(1L, 2L, 3L);
//
//        List<PostRedisDto> redisPosts = new ArrayList<>(List.of(
//            createPostRedisDto(1L),
//            createPostRedisDto(4L)
//        ));
//
//        Post dbPost = Post.builder()
//            .id(4L)
//            .content("Missing Post Content")
//            .authorId(USER_ID)
//            .published(true)
//            .deleted(false)
//            .createdAt(LocalDateTime.now().minusHours(1))
//            .updatedAt(LocalDateTime.now())
//            .build();
//
//        when(postRepository.findAllById(List.of(4L))).thenReturn(List.of(dbPost));
//        when(redisUserRepository.checkUserExist(USER_ID)).thenReturn(false);
////        when(redisFeedRepository.getFeedPostIds(USER_ID, 0L, PAGE_SIZE)).thenReturn(feedPostIds);
//        when(redisPostRepository.getPosts(feedPostIds)).thenReturn(redisPosts);
//        when(postMapper.toRedisEntityList(anyList())).thenReturn(List.of(createPostRedisDto(4L)));
//        when(redisUserRepository.getUsers(anyList())).thenReturn(createUsers(List.of(USER_ID)));
//        when(feedMapper.toFeedResponseDto(any(), any())).thenReturn(
//            new FeedResponseDto("L", "M", LocalDateTime.now()));
//
//        List<FeedResponseDto> result = feedService.getUserFeed(USER_ID, 0L);
//
//        verify(postRepository).findAllById(List.of(4L));
//        assertEquals(3, result.size());
//        verify(redisPostRepository).cachePosts(anyList());
//    }
//
//    @Test
//    void getUserFeed_WhenAuthorsMissing_ShouldHandleGracefully() {
//        List<Long> postIds = List.of(1L);
//        PostRedisDto post = createPostRedisDto(1L);
//
//        when(redisUserRepository.checkUserExist(USER_ID)).thenReturn(false);
////        when(redisFeedRepository.getFeedPostIds(USER_ID, 0L, PAGE_SIZE)).thenReturn(postIds);
//
//        when(redisPostRepository.getPosts(postIds)).thenReturn(new ArrayList<>(List.of(post)));
//
//        when(redisUserRepository.getUsers(List.of(USER_ID))).thenReturn(Collections.emptyList());
//
//        when(feedMapper.toFeedResponseDto(any(PostRedisDto.class), isNull()))
//            .thenReturn(new FeedResponseDto("Mixa", "content", LocalDateTime.now()));
//
//        List<FeedResponseDto> result = feedService.getUserFeed(USER_ID, 0L);
//
//        assertNotNull(result);
//        assertEquals(1, result.size());
//        verify(feedMapper).toFeedResponseDto(post, null);
//    }
//
//    private List<PostRedisDto> createRedisPosts(List<Long> ids) {
//        return ids.stream()
//            .map(this::createPostRedisDto)
//            .toList();
//    }
//
//    private PostRedisDto createPostRedisDto(Long id) {
//        return PostRedisDto.builder()
//            .id(id)
//            .authorId(USER_ID)
//            .content("Content " + id)
//            .build();
//    }
//
//    private List<UserRedisDto> createUsers(List<Long> userIds) {
//        return userIds.stream()
//            .map(id -> new UserRedisDto(id, "User " + id, "user" + id + "@test.com"))
//            .toList();
//    }
}
