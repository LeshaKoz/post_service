package faang.school.postservice.service;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostCacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostCacheService {
    private final PostCacheRepository cacheRepository;

    @Value("${app.newsfeed.cache.post-ttl}")
    private Duration postTtl;

    public void cachePost(Post post) {
        cacheRepository.save(post, postTtl);
    }

    public Optional<Post> getCachedPost(Long postId) {
        return cacheRepository.findById(postId);
    }

    public void removePostFromCache(Long postId) {
        cacheRepository.deleteById(postId);
    }

    public void cleanupExpiredPosts() {
        log.info("Starting cache cleanup for expired posts");
        cacheRepository.removeExpiredPosts();
        log.info("Cache cleanup completed");
    }
}
