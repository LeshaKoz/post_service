package faang.school.postservice.service.cache;

import faang.school.postservice.dto.posts.PostRedis;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class RedisCache {
    private final PostRedisRepository repository;

    @Async("cachePostExecutor")
    public void cachePost(Post post) {
        log.info("caching post...");
        PostRedis postRedis = new PostRedis(post);
        repository.save(postRedis);
    }
}

