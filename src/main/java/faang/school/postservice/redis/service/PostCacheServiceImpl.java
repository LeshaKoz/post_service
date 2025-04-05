package faang.school.postservice.redis.service;

import faang.school.postservice.dto.comment.CommentRedisDto;
import faang.school.postservice.dto.post.PostCacheDto;
import faang.school.postservice.mapper.comment.CommentRedisMapper;
import faang.school.postservice.mapper.post.PostCacheMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.event.kafka.PostEventKafka;
import faang.school.postservice.redis.model.entity.PostCache;
import faang.school.postservice.redis.repository.PostCacheRedisRepository;
import faang.school.postservice.repository.CommentRepository;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PostCacheServiceImpl implements PostCacheService {

    @Value("${cache.post-ttl}")
    private long postTtl;

    @Value("${cache.post.fields.views}")
    private String postCacheViewsField;

    @Value("${cache.post.fields.number-of-likes}")
    private String numberOfLikesField;

    @Value("${cache.post.prefix}")
    private String cachePrefix;

    @Value("${post-comments.size}")
    private int postCommentsSize;

    private final PostCacheRedisRepository postCacheRedisRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final PostCacheMapper postCacheMapper;
    private final RedissonClient redissonClient;
    private final FeedCacheService feedCacheService;
    private final CommentRepository commentRepository;
    private final CommentRedisMapper commentRedisMapper;

    @Autowired
    public PostCacheServiceImpl(PostCacheRedisRepository postCacheRedisRepository,
                                @Qualifier("redisCacheTemplate")
                                RedisTemplate<String, Object> redisTemplate,
                                PostCacheMapper postCacheMapper,
                                RedissonClient redissonClient,
                                FeedCacheService feedCacheService,
                                CommentRepository commentRepository,
                                CommentRedisMapper commentRedisMapper) {

        this.postCacheRedisRepository = postCacheRedisRepository;
        this.redisTemplate = redisTemplate;
        this.postCacheMapper = postCacheMapper;
        this.redissonClient = redissonClient;
        this.feedCacheService = feedCacheService;
        this.commentRepository = commentRepository;
        this.commentRedisMapper = commentRedisMapper;
    }

    @Override
    public void savePostToCache(PostCacheDto post) {
        log.info("Saving post with ID {} to cache", post.getId());

        PostCache postCache = postCacheMapper.toPostCache(post);
        postCacheRedisRepository.save(postCache);

        String key = createPostCacheKey(post.getId());

        redisTemplate.expire(key, Duration.ofSeconds(postTtl));
        log.info("Post with ID {} saved to cache with key: {} and TTL: {} seconds", post.getId(), key, postTtl);
    }

    @Override
    public void addPostView(PostCacheDto post) {
        String lockKey = "lock:" + post.getId();
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock();
        try {
            log.debug("Lock acquired for postId: {}", post.getId());
            incrementNumberOfPostViews(post.getId());
            log.info("Successfully incremented views for postId: {}", post.getId());
        } finally {
            lock.unlock();
            log.debug("Lock released for postId: {}", post.getId());
        }
    }


    private void incrementNumberOfPostViews(Long postId) {
        redisTemplate.opsForHash()
                .increment(createPostCacheKey(postId), String.valueOf(postCacheViewsField), 1);
    }

    private String createPostCacheKey(Long postId) {
        return cachePrefix + postId;
    }


    @Override
    public void updateFeedsInCache(PostEventKafka event) {
        List<CompletableFuture<Void>> features = event.getFollowerIds().stream()
                .map(followerId -> feedCacheService.getAndSaveFeed(followerId, event.getPostId()))
                .toList();
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(features.toArray(new CompletableFuture[0]));
        allFutures.join();
    }

    @Override
    public CompletableFuture<Void> saveAllPostsToCache(List<PostCacheDto> posts) {

        List<PostCache> newPostCaches = filterNewPosts(posts).stream()
                .map(post -> {
                    PostCache postCache = postCacheMapper.toPostCache(post);
                    TreeSet<CommentRedisDto> lastThreeComments = getLastThreeComments(post.getId());
                    postCache.setComments(lastThreeComments);
                    return postCache;
                })
                .toList();

        if (!newPostCaches.isEmpty()) {
            log.info("Saving {} new posts to cache.", newPostCaches.size());
            return CompletableFuture.runAsync(() -> {
                postCacheRedisRepository.saveAll(newPostCaches);
                setTtlForPosts(newPostCaches);
            });
        }

        log.info("No new posts to cache.");
        return CompletableFuture.completedFuture(null);
    }

    private void setTtlForPosts(List<PostCache> newPostCaches) {
        newPostCaches.forEach(postCache -> {
            String key = createPostCacheKey(postCache.getId());
            redisTemplate.expire(key, Duration.ofSeconds(postTtl));
            log.info("Set TTL for post {} in cache.", postCache.getId());
        });
    }

    private List<PostCacheDto> filterNewPosts(List<PostCacheDto> posts) {
        List<String> keys = posts.stream()
                .map(post -> "post:" + post.getId())
                .toList();

        List<Object> results = redisTemplate.opsForValue().multiGet(keys);

        if (results == null) {
            log.warn("Failed to retrieve keys from Redis. Assuming all posts are new.");
            return posts;
        }

        return posts.stream()
                .filter(user -> {
                    int index = posts.indexOf(user);
                    return results.get(index) == null;
                })
                .toList();
    }

    private TreeSet<CommentRedisDto> getLastThreeComments(long postId) {
        List<Comment> comments = commentRepository.findAllByPostId(postId);

        return comments.stream()
                .sorted(Comparator.comparing(Comment::getCreatedAt).reversed())
                .limit(3)
                .map(commentRedisMapper::toCommentRedisDto)
                .collect(Collectors.toCollection(TreeSet::new));
    }
}
