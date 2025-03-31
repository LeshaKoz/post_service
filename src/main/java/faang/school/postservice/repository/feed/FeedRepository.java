package faang.school.postservice.repository.feed;

import faang.school.postservice.config.feed.NewsFeedProperties;
import faang.school.postservice.dto.feed.FeedItemDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.mapper.post.PostMapper;
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
    private final RedisTemplate<String, FeedItemDto> FeedItemRedisTemplate;
    private final PostMapper postMapper;


    public Set<FeedItemDto> feedItems(Long userId, int pageNum) {

        long pageSize = newsFeedProperties.pageSize();
        String prefix = newsFeedProperties.prefix();
        /*Set<FeedItemResponseDto> result = FeedItemRedisTemplate.opsForZSet().range(
                prefix + ":" + userId,
                pageNum * pageSize,
                -(pageNum + 1) * pageSize - 1);*/
        Set<FeedItemDto> result = FeedItemRedisTemplate.opsForZSet().range(
                prefix + ":" + userId,
                0,
                -1);
        log.debug("Raw data from Redis: {}", FeedItemRedisTemplate.opsForZSet()
                .range(prefix + ":" + userId, 0, -1));
        return result;
    }

    public void addPostToFollowersFeeds(List<Long> followersIds, PostResponseDto post) {
        FeedItemDto feedItem = FeedItemDto.builder()
                .postLikesCounter(0)
                //.post(new FeedItemResponseDto.Post(post.id(), post.content(), post.publishedAt(), post.authorId()))
                .postId(post.id())
                .build();

        followersIds.forEach(userId -> addPost(userId, post));
    }

    private void addPost(long userId, PostResponseDto post) {
        long postId = post.id();
        log.info("Adding feed item for user {}, post {}", userId, postId);
        //log.info("Adding feed item for user {}, post {}", userId, feedItemResponseDto.postId());
        int setSize = newsFeedProperties.maxPosts();
        String prefix = newsFeedProperties.prefix();
        double score = post.publishedAt().toInstant(ZoneOffset.UTC).toEpochMilli();
        //double score = feedItemResponseDto.publishedAt().toInstant(ZoneOffset.UTC).toEpochMilli();
        FeedItemDto feedItemDto = FeedItemDto.builder()
                .postId(postId)
                .build();
        FeedItemRedisTemplate.opsForZSet().add(
                prefix + ":" + userId,
                feedItemDto,
                score);

        Set<FeedItemDto> result = FeedItemRedisTemplate.opsForZSet()
                .range(prefix + ":" + userId, 0, -1);

        FeedItemRedisTemplate.opsForZSet().removeRange(
                prefix + ":" + userId,
                0,
                -newsFeedProperties.maxPosts() - 1);
        result = FeedItemRedisTemplate.opsForZSet()
                .range(prefix + ":" + userId, 0, -1);

        log.info("Post {} saved to newsfeed for user {}", postId, userId);
        //log.info("Post {} saved to newsfeed for user {}", feedItemResponseDto.postId(), userId);
    }

}
