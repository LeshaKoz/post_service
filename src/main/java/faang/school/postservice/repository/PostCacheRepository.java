package faang.school.postservice.repository;

import faang.school.postservice.model.Post;

import java.time.Duration;
import java.util.Optional;

public interface PostCacheRepository {
    void save(Post post, Duration ttl);
    Optional<Post> findById(Long id);
    void deleteById(Long id);
    void removeExpiredPosts();
}
