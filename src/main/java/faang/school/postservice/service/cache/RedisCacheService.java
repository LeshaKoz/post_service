package faang.school.postservice.service.cache;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentReadDto;
import faang.school.postservice.dto.post.PostReadDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.cache.PostCache;
import faang.school.postservice.model.cache.UserCache;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.cache.RedisPostRepository;
import faang.school.postservice.repository.cache.RedisUserRepository;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class RedisCacheService {

    private final RedisUserRepository redisUserRepository;
    private final RedisPostRepository redisPostRepository;
    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;
    private final RedissonClient redissonClient;
    private final PostMapper postMapper;

    @Value("${spring.data.redis.post-cache.comments-in-post:3}")
    private int maxCommentsInPostCache;

    @Value("${spring.data.redis.redisson-client.lock-cache-post}")
    private String lockCachePost;

    @Value("${spring.data.redis.redisson-client.key-for-version}")
    private String versionedKey;

    @Value("${spring.data.redis.redisson-client.name-version}")
    private String version;

    @Value("${spring.data.redis.redisson-client.start-num-for-version}")
    private int startNumForKey;

    @Async(value = "caching")
    public void saveAuthorComment(CommentReadDto commentReadDto) {
        saveAuthor(commentReadDto.authorId());
    }

    @Async(value = "caching")
    public void saveAuthorPost(PostReadDto postReadDto) {
        saveAuthor(postReadDto.getAuthorId());
    }

    @Async(value = "caching")
    public void savePost(PostReadDto postReadDto) {
        redisPostRepository.save(PostCache.builder()
                .postId(postReadDto.getId())
                .authorId(postReadDto.getAuthorId())
                .content(postReadDto.getContent())
                .build());
    }

    public void addLikeToPost(long postId, long likeId) {
        updatePostWithLock(postId, postCache -> {
            postCache.getLikeIds().add(likeId);
            postCache.incrementLikes();
        });
    }

    public void addPostView(long postId) {
        updatePostWithLock(postId, PostCache::incrementViews);
    }

    public void addCommentToPostCache(long postId, String comment) {
        updatePostWithLock(postId, postCache -> {
            LinkedHashSet<String> comments = postCache.getComments();
            if (comments == null) {
                comments = new LinkedHashSet<>();
            }
            checkCapacity(comments);
            comments.add(comment);
            postCache.setComments(comments);
        });
    }

    public List<PostReadDto> getPostCacheByIds(List<Long> postsId) {
        return redisPostRepository.findAllById(postsId).stream()
                .map(postMapper::toPostReadDto)
                .toList();
    }

    public void initVersion(PostCache postCache) {
        RMap<String, Integer> versionMap = redissonClient.getMap(version);
        versionMap.putIfAbsent(versionedKey, startNumForKey);
        postCache.setVersion(versionMap);
    }

    private void saveAuthor(long authorId) {
        redisUserRepository.save(UserCache.builder()
                .userId(authorId)
                .userName(userServiceClient
                        .getUser(authorId)
                        .username())
                .build());
    }

    private void updatePostWithLock(long postId, Consumer<PostCache> updateFunction) {
        RLock lock = redissonClient.getLock(lockCachePost + postId);
        try {
            if (lock.tryLock(2, 1, TimeUnit.SECONDS)) {
                try {
                    PostCache postCache = getPostCache(postId);
                    updateFunction.accept(postCache);
                    redisPostRepository.save(postCache);
                } finally {
                    lock.unlock();
                }
            } else {
                throw new RuntimeException("Не удалось получить блокировку для поста с id: " + postId);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Ошибка блокировки", e);
        }
    }

    private void checkCapacity(Set<String> comments) {
        if (comments.size() >= maxCommentsInPostCache) {
            Iterator<String> iterator = comments.iterator();
            if (iterator.hasNext()) {
                iterator.next();
                iterator.remove();
            }
        }
    }

    private PostCache getPostCache(long postId) {
        return redisPostRepository.findById(postId)
                .orElseGet(() -> {
                    PostCache postCache = getPostCacheFromDB(postId);
                    redisPostRepository.save(postCache);
                    return postCache;
                });
    }

    private PostCache getPostCacheFromDB(long postId) {
        PostCache postCache = postMapper.toPostCache(postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException(
                        String.format("Нет такого поста с id: %d", postId))));
        RMap<String, Integer> versionMap = redissonClient.getMap(version);

        versionMap.putIfAbsent(versionedKey, startNumForKey);
        postCache.setVersion(versionMap);

        return postCache;
    }
}
