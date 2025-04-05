package faang.school.postservice.redis.service;

import java.util.concurrent.CompletableFuture;

public interface FeedCacheService {

    CompletableFuture<Void> getAndSaveFeed(Long feedId, Long postId);
}
