package faang.school.postservice.service.redis;

import faang.school.postservice.model.cache.PostCache;
import faang.school.postservice.repository.cache.RedisPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RedisPostService {
    private final RedisPostRepository redisPostRepository;

    @Value("${spring.data.redis.cache.TTL.post-cache}")
    private long postCacheTTL;

    public void savePostToCache(PostCache postCache) {
        log.debug("Saving post cache: {}", postCache);

        postCache.setTtl(postCacheTTL);
        redisPostRepository.save(postCache);
    }

    public PostCache getPostCache(long postId) {
        log.debug("Getting post cache for post id: {}", postId);
        return redisPostRepository.findById(postId).orElse(null);
    }

}
