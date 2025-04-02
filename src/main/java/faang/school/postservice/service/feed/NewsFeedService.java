package faang.school.postservice.service.feed;

import faang.school.postservice.dto.feed.PostFeedReadDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.event.kafka.KafkaPostEventDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.FeedMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.cache.FeedCache;
import faang.school.postservice.model.cache.PostCache;
import faang.school.postservice.model.cache.UserCache;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.redis.RedisFeedRepository;
import faang.school.postservice.repository.redis.RedisPostRepository;
import faang.school.postservice.repository.redis.RedisUserRepository;
import faang.school.postservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsFeedService {

    @Value("${news-feed.max-feed-size:500}")
    private int maxFeedSize;
    @Value("${news-feed.batch-size:20}")
    private int postBatchSize;
    @Value("${spring.data.kafka.key.post}")
    private String postKey;
    @Value("${spring.data.kafka.key.feed}")
    private String feedKey;

    private final RedisTemplate<String, String> redisTemplate;
    private final FeedMapper feedMapper;
    private final RedisUserRepository redisUserRepository;
    private final RedisPostRepository redisPostRepository;
    private final RedisFeedRepository redisFeedRepository;
    private final PostRepository postRepository;
    private final UserService userService;

    public List<PostFeedReadDto> getFeed(long userId, Long lastPostId) {
        Optional<FeedCache> optionalFeedCache = redisFeedRepository.findById(userId);
        if (optionalFeedCache.isEmpty()) {
            return new ArrayList<>();
        }

        FeedCache feedCache = optionalFeedCache.get();
        List<Long> postIds = feedCache.getPostBatch(postBatchSize, lastPostId);
        return postIds.stream()
                .map(this::getPostFeedDto)
                .toList();
    }

    public void addAuthorToCacheByComment(Comment comment) {
        long authorId = comment.getId();
        String username = userService.getUserDtoById(authorId).username();

        UserCache userCache = new UserCache(authorId, username);
        redisUserRepository.save(userCache);
    }

    public  void updatePostCountView(Long postId, Long viewCount) {
        String key = postKey + ":viewCount:" + postId;
        redisTemplate.opsForValue().increment(key, viewCount);
    }

    public void addPostToFeed(KafkaPostEventDto postEventDto, long followerId) {
        String redisKey = feedKey + ":" + followerId;
        String postId = String.valueOf(postEventDto.getPostId());
        long timestamp = System.currentTimeMillis();
        try {
            redisTemplate.opsForZSet().add(redisKey, postId, timestamp);
            redisTemplate.opsForZSet().removeRange(redisKey, 0, -maxFeedSize - 1);
            log.info("Добавлен пост {} в Ленту новостей для пользователя {}", postEventDto.getPostId(), followerId);
        } catch (Exception e) {
            log.error("Ошибка добавления поста в Ленту новостей для пользователя {}: {}", followerId, e.getMessage(), e);
        }
    }

    private PostFeedReadDto getPostFeedDto(long postId) {
        PostFeedReadDto postFeedReadDto;

        PostCache postCache = redisPostRepository.findById(postId).orElse(null);
        if (postCache != null) {
            postFeedReadDto = feedMapper.toPostFeedDto(postCache);
        } else {
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new EntityNotFoundException("Пост с ID %s не найден".formatted(postId)));
            postFeedReadDto = feedMapper.toPostFeedDto(post);
        }

        if (postFeedReadDto.getAuthorId() != null) {
            UserCache userCache = redisUserRepository.findById(postFeedReadDto.getAuthorId()).orElse(null);
            if (userCache != null) {
                postFeedReadDto.setUsername(userCache.getUsername());
            } else {
                UserDto userDto = userService.getUserDtoById(postFeedReadDto.getAuthorId());
                postFeedReadDto.setUsername(userDto.username());
            }
        }
        return postFeedReadDto;
    }
}
