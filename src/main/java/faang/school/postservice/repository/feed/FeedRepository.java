package faang.school.postservice.repository.feed;

import faang.school.postservice.config.feed.NewsFeedProperties;
import faang.school.postservice.dto.feed.FeedItemResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FeedRepository {

    private final NewsFeedProperties newsFeedProperties;

    //private final RedisTemplate<String, Object> redisTemplate;
    private final RedisTemplate<String, FeedItemResponseDto> FeedItemRedisTemplate;


    public Set<FeedItemResponseDto> feedItems(Long userId, int pageNum) {

        long pageSize = newsFeedProperties.pageSize();
        String prefix = newsFeedProperties.prefix();
        /*Set<FeedItemResponseDto> result = FeedItemRedisTemplate.opsForZSet().range(
                prefix + ":" + userId,
                pageNum * pageSize,
                -(pageNum + 1) * pageSize - 1);*/
        Set<FeedItemResponseDto> result = FeedItemRedisTemplate.opsForZSet().range(
                prefix + ":" + userId,
                0,
                -1);
        log.debug("Raw data from Redis: {}", FeedItemRedisTemplate.opsForZSet()
                .range(prefix + ":" + userId, 0, -1));
        return result;
    }

    public void addPostToFollowersFeeds(List<Long> followersIds, FeedItemResponseDto feedItemResponseDto) {
        followersIds.forEach(userId -> addPost(userId, feedItemResponseDto));
    }

    private void addPost(long userId, FeedItemResponseDto feedItemResponseDto) {
        log.info("Adding feed item for user {}, post {}", userId, feedItemResponseDto.post().postId());
        //log.info("Adding feed item for user {}, post {}", userId, feedItemResponseDto.postId());
        int setSize = newsFeedProperties.maxPosts();
        String prefix = newsFeedProperties.prefix();
        double score = feedItemResponseDto.post().publishedAt().toInstant(ZoneOffset.UTC).toEpochMilli();
        //double score = feedItemResponseDto.publishedAt().toInstant(ZoneOffset.UTC).toEpochMilli();
        FeedItemRedisTemplate.opsForZSet().add(
                prefix + ":" + userId,
                feedItemResponseDto,
                score);

        Set<FeedItemResponseDto> result = FeedItemRedisTemplate.opsForZSet()
                .range(prefix + ":" + userId, 0, -1);

        FeedItemRedisTemplate.opsForZSet().removeRange(
                prefix + ":" + userId,
                0,
                -newsFeedProperties.maxPosts() - 1);
        result = FeedItemRedisTemplate.opsForZSet()
                .range(prefix + ":" + userId, 0, -1);

        log.info("Post {} saved to newsfeed for user {}", feedItemResponseDto.post().postId(), userId);
        //log.info("Post {} saved to newsfeed for user {}", feedItemResponseDto.postId(), userId);
    }

}
