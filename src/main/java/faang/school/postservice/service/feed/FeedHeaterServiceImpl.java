package faang.school.postservice.service.feed;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.feed.HeatTask;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedHeaterServiceImpl implements FeedHeaterService {

    private final UserServiceClient userServiceClient;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final PostService postService;
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${kafka.feed.heat-tasks.topic}")
    private String heatTasksTopic;

    @Value("${heat.batch-size}")
    private int batchSize;

    @Async("cacheFixedThreadPool")
    @Override
    public void startHeat() {
        List<Long> allUserIds = userServiceClient.getUserIds();

        String taskId = UUID.randomUUID().toString();
        int batchCount = 0;
        for (int i = 0; i < allUserIds.size(); i += batchSize) {
            List<Long> batch = allUserIds.subList(i, Math.min(i + batchSize, allUserIds.size()));
            HeatTask task = new HeatTask(batch, taskId + "-" + batchCount++);
            String json = mapObjectToJson(task);
            kafkaTemplate.send(heatTasksTopic, json);
        }
    }

    @Async("cacheFixedThreadPool")
    @Override
    public void cacheHeat(HeatTask task) {
        for (Long userId : task.getUserIds()) {
            try {
                List<String> followers = userServiceClient.getFollowersByUserId(userId).stream()
                        .map(UserDto::getId)
                        .map(String::valueOf)
                        .toList();

                List<PostResponseDto> posts = postService.getLatestPosts(followers, 500);

                saveToRedis(String.valueOf(userId), posts);
            } catch (Exception e) {
                log.error("Error processing user {}", userId, e);
            }
        }
    }

    private void saveToRedis(String userId, List<PostResponseDto> posts) {
        String feedKey = "user:feed:" + userId;
        redisTemplate.delete(feedKey);
        posts.forEach(post -> {
            double score = -post.createdAt().toEpochSecond(ZoneOffset.UTC);
            redisTemplate.opsForZSet().add(feedKey, String.valueOf(post.id()), score);

            String postKey = "post:" + post.id();
            redisTemplate.opsForHash().putAll(postKey, Map.of(
                    "authorId", post.authorId(),
                    "content", post.content(),
                    "createdAt", post.createdAt().toString(),
                    "likesCount", String.valueOf(post.likesCount()),
                    "viewsCount", String.valueOf(post.viewsCount())
            ));
            String authorKey = "user:" + post.authorId();
            if (!Boolean.TRUE.equals(redisTemplate.hasKey(authorKey))) {
                UserDto author = userServiceClient.getUser(post.authorId());
                redisTemplate.opsForHash().putAll(authorKey, Map.of(
                        "username", author.getUsername()
                ));
            }
        });
    }

    private String mapObjectToJson(HeatTask task) {
        try {
            return objectMapper.writeValueAsString(task);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
