package faang.school.postservice.service;

import faang.school.postservice.dto.comment.CommentForNewsFeedDto;
import faang.school.postservice.dto.post.PostForNewsFeedDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.dto.user.UserForNewsFeedResponseDto;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.message.event.CommentEvent;
import faang.school.postservice.message.event.PostEvent;
import faang.school.postservice.model.cache.CommentCache;
import faang.school.postservice.model.cache.Feed;
import faang.school.postservice.model.cache.PostCache;
import faang.school.postservice.model.cache.UserCache;
import faang.school.postservice.repository.cache.RedisPostRepository;
import faang.school.postservice.repository.cache.RedisUserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.integration.support.locks.ExpirableLockRegistry;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.Lock;

@Service
@RequiredArgsConstructor
@Slf4j
public class NewsFeedService {

    private final Feed feed;
    private final TaskExecutor postEventProcessingPool;
    private final CommentMapper commentMapper;
    private final ExpirableLockRegistry redisLockRegistry;
    private final RedisPostRepository redisPostRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisUserRepository redisUserRepository;

    @Value("${feed.posts-per-page}")
    private int postsPerPage;

    @Value("${feed.comments-per-post}")
    private int commentsPerPost;

    @Value("${spring.data.redis.lock-prefix.posts}")
    private String postsLockPrefix;

    @Value("${spring.data.redis.hash-prefix.posts:posts}")
    private String redisPostsHashPrefix;

    public CompletableFuture<Void> addPostToFollowersFeedInCache(PostEvent postEvent) {
        log.info("Trying to add post to subscribers feed {}", postEvent);
        return CompletableFuture.allOf(postEvent.followerIds().stream()
                .map(followerId -> CompletableFuture.runAsync(() -> {
                    log.debug("Adding post to user {}", followerId);
                    feed.addPostToFeed(followerId, postEvent.postId());
                }, postEventProcessingPool))
                .toArray(CompletableFuture[]::new));
    }

    public CompletableFuture<Void> addCommentToCache(CommentEvent commentEvent) {
        return CompletableFuture.runAsync(() -> {
            log.info("Trying to add comment {} to post under id {}", commentEvent.content(), commentEvent.postId());

            String lockKey = postsLockPrefix + commentEvent.postId();
            Lock lock = redisLockRegistry.obtain(lockKey);
            lock.lock();

            try {
                PostCache post = redisPostRepository.findById(commentEvent.postId())
                        .orElseThrow(() -> new EntityNotFoundException("Post with id %s not found"
                                .formatted(commentEvent.postId())));

                CommentCache newComment = commentMapper.toCommentCache(commentEvent);
                post.addComment(newComment, commentsPerPost);
                redisPostRepository.save(post);

            } finally {
                lock.unlock();
            }
        });
    }

    public void incrementLikeCount(long postId) {
        log.info("Incrementing like count for post {}", postId);
        String redisKey = redisPostsHashPrefix + ":" + postId;
        redisTemplate.opsForHash().increment(redisKey, "countOfLikes", 1);
    }

    public void incrementViewCount(long postId) {
        log.info("Incrementing view count for post {}", postId);
        String redisKey = redisPostsHashPrefix + ":" + postId;
        redisTemplate.opsForHash().increment(redisKey, "countOfViews", 1);
    }
}

