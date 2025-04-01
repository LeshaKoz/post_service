package faang.school.postservice.service.news_feed;

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
import jakarta.persistence.EntityNotFoundException;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class FeedService {
    private final RedisFeedRepository redisFeedRepository;
    private final RedisUserRepository redisUserRepository;
    private final RedisPostRepository redisPostRepository;
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final FeedMapper feedMapper;
    private static final int PAGE_SIZE = 20;

    public List<FeedResponseDto> getUserFeed(Long userId, Long afterPostId) {
        if (!redisUserRepository.checkUserExist(userId)) {
            throw new EntityNotFoundException("user not found " + userId);
        }

        List<Long> feedPostIds = getFeedPostIds(userId, afterPostId);
        List<PostRedisDto> postsInRedis = redisPostRepository.getPosts(feedPostIds);

        if (postsInRedis.size() < PAGE_SIZE) {
            List<Long> postInRedisIds = postsInRedis.stream()
                .map(PostRedisDto::getId)
                .toList();
            feedPostIds.removeAll(postInRedisIds);

            List<Post> allById = postRepository.findAllById(feedPostIds);
            List<PostRedisDto> postRedisDto = postMapper.toRedisEntityList(allById);
            postsInRedis.addAll(postRedisDto);
        }

        redisPostRepository.cachePosts(postsInRedis);
        List<UserRedisDto> users = redisUserRepository.getUsers(getAuthorIds(postsInRedis));
        Map<Long, UserRedisDto> userMap = users.stream()
            .collect(Collectors.toMap(UserRedisDto::id, user -> user));

        return postsInRedis.stream()
            .map(post -> feedMapper.toFeedResponseDto(post, userMap.get(post.getAuthorId())))
            .collect(Collectors.toList());
    }

    private List<Long> getFeedPostIds(Long userId, Long afterPostId) {
        if (afterPostId == null) {
            return redisFeedRepository.getLatestPosts(userId, PAGE_SIZE);
        }

        PostRedisDto postById = redisPostRepository.findPostById(afterPostId);
        if (postById != null) {
            return redisFeedRepository.getPostsAfterTimeStamp(userId,
                postById.getPublishedAt().toEpochSecond(ZoneOffset.UTC), PAGE_SIZE);
        }

        Post post = postRepository.findById(afterPostId)
            .orElseThrow(() -> new NoSuchElementException("Post not found"));
        return redisFeedRepository.getPostsAfterTimeStamp(userId,
            post.getPublishedAt().toEpochSecond(ZoneOffset.UTC), PAGE_SIZE);
    }

    private List<Long> getAuthorIds(List<PostRedisDto> postsInRedis) {
        return postsInRedis.stream()
            .map(PostRedisDto::getAuthorId)
            .distinct()
            .collect(Collectors.toList());
    }
}
