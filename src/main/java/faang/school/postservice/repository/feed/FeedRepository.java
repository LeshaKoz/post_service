package faang.school.postservice.repository.feed;

import faang.school.postservice.config.feed.NewsFeedProperties;
import faang.school.postservice.dto.feed.FeedItemDto;
import faang.school.postservice.dto.post.PostResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Repository;

import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FeedRepository {

    private final NewsFeedProperties newsFeedProperties;
    private final RedisTemplate<String, FeedItemDto> FeedItemRedisTemplate;

    public Set<FeedItemDto> feedItems(Long userId, int pageNum) {

        final long pageSize = newsFeedProperties.pageSize();
        String zsetKey = getFeedItemHashKey(userId);

        /*Set<FeedItemResponseDto> result = FeedItemRedisTemplate.opsForZSet().range(
                zsetKey,
                pageNum * pageSize,
                -(pageNum + 1) * pageSize - 1);*/
        Set<FeedItemDto> result = FeedItemRedisTemplate.opsForZSet().range(
                zsetKey,
                0,
                -1);
        log.debug("Raw data from Redis: {}", FeedItemRedisTemplate.opsForZSet()
                .range(getFeedItemHashKey(userId), 0, -1));
        return result;
    }

    public void addPostToFollowersFeeds(List<Long> followersIds, PostResponseDto post) {
        FeedItemDto feedItem = FeedItemDto.builder()
                //.postLikesCounter(0)
                .postId(post.id())
                .build();

        followersIds.forEach(userId -> addPostToFollowerFeed(userId, post));
    }

    private void addPostToFollowerFeed(long userId, PostResponseDto post) {
        long postId = post.id();
        log.info("Adding feed item for user {}, post {}", userId, postId);
        //log.info("Adding feed item for user {}, post {}", userId, feedItemResponseDto.postId());
        int setSize = newsFeedProperties.maxPosts();
        double score = post.publishedAt().toInstant(ZoneOffset.UTC).toEpochMilli();
        //double score = feedItemResponseDto.publishedAt().toInstant(ZoneOffset.UTC).toEpochMilli();
        FeedItemDto feedItemDto = FeedItemDto.builder()
                .postId(postId)
                .build();
        addOrUpdateFeedItemInCache(userId, feedItemDto, score);
    }

    private Boolean addOrUpdateFeedItemInCache(long userId, FeedItemDto feedItemDto, double score) {

        final int MAX_FEED_SIZE = newsFeedProperties.maxPosts();
        String zsetKey = getFeedItemHashKey(userId);

        return FeedItemRedisTemplate.execute(new SessionCallback<Boolean>() {
             @Override
             public <K, V> Boolean execute(RedisOperations<K, V> operations) throws DataAccessException {

                 operations.multi();

                 operations.opsForZSet().add((K) zsetKey, (V) feedItemDto, score);
                 // Удаляем все, кроме последних N
                 operations.opsForZSet().removeRange((K) zsetKey, 0, -MAX_FEED_SIZE - 1);

                 List<Object> results = operations.exec();
                 return !results.isEmpty();
             }
        });




        /*Set<FeedItemDto> result = FeedItemRedisTemplate.opsForZSet()
                .range(zsetKey, 0, -1);

        FeedItemRedisTemplate.opsForZSet().removeRange(
                zsetKey,
                0,
                -newsFeedProperties.maxPosts() - 1);
        result = FeedItemRedisTemplate.opsForZSet()
                .range(zsetKey, 0, -1);*/


        //return true;

        //log.info("Post {} saved to newsfeed for user {}", feedItemResponseDto.postId(), userId);
    }

    /*public getFeedItem(String key) {

    }*/

    private String getFeedItemHashKey(long userId) {
        String prefix = newsFeedProperties.prefix();
        return prefix + ":" + userId;
    }

    private String getFeedItemHashIndexKey(long userId) {
        String prefix = newsFeedProperties.prefix();
        return prefix + ":" + userId + ":feed_index";
    }

}
