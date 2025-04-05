package faang.school.postservice.service.feed;

import com.google.common.collect.Lists;
import faang.school.postservice.config.redis.FeedProperties;
import faang.school.postservice.event.post.PostCreatedEvent;
import faang.school.postservice.event.post.PostDeletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class FeedService {
    private final RedisTemplate<String, String> feedRedisTemplate;
    private final FeedProperties properties;
    private static final String FEED_KEY_PATTERN = "user:%d:feed";
    private static final String POST_FEEDS_INDEX_PATTERN = "post:%d:feeds";
    private static final long SECONDS_IN_A_DAY = 86400L;

    @Value("${spring.data.redis.feed.batch-size}")
    private int batchSize;

    public void addToFeed(PostCreatedEvent event) {
        double score = event.getCreatedAt().toEpochMilli();
        String postId = String.valueOf(event.getPostId());
        String postFeedsKey = String.format(POST_FEEDS_INDEX_PATTERN, event.getPostId());

        List<List<Long>> batches = Lists.partition(event.getFollowerIds(), batchSize);

        for (List<Long> batch : batches) {
            feedRedisTemplate.executePipelined((RedisCallback<Void>) connection -> {
                for (Long userId : batch) {
                    String feedKey = String.format(FEED_KEY_PATTERN, userId);

                    connection.zAdd(feedKey.getBytes(), score, postId.getBytes());
                    connection.zRemRange(feedKey.getBytes(), 0, -properties.getMaxSize() - 1);
                    connection.expire(feedKey.getBytes(), properties.getTtlDays() * SECONDS_IN_A_DAY);

                    connection.sAdd(postFeedsKey.getBytes(), feedKey.getBytes());
                }

                connection.expire(postFeedsKey.getBytes(), properties.getTtlDays() * SECONDS_IN_A_DAY);
                return null;
            });
        }
    }

    public void removeFromFeed(PostDeletedEvent event) {
        String postId = String.valueOf(event.getPostId());
        String postFeedsKey = String.format(POST_FEEDS_INDEX_PATTERN, event.getPostId());
        byte[] postIdBytes = postId.getBytes();

        Set<byte[]> feedKeysBytes = feedRedisTemplate.execute(
                (RedisCallback<Set<byte[]>>) connection -> connection.sMembers(postFeedsKey.getBytes()));

        if (feedKeysBytes == null || feedKeysBytes.isEmpty()) {
            return;
        }

        List<List<byte[]>> batches = Lists.partition(
                new ArrayList<>(feedKeysBytes),
                batchSize
        );

        for (List<byte[]> batch : batches) {
            feedRedisTemplate.executePipelined((RedisCallback<Void>) connection -> {
                for (byte[] feedKeyBytes : batch) {
                    connection.zRem(feedKeyBytes, postIdBytes);
                }
                return null;
            });
        }

        feedRedisTemplate.delete(postFeedsKey);
    }
}