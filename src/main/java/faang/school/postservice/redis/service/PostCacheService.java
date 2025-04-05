package faang.school.postservice.redis.service;

import faang.school.postservice.dto.post.PostCacheDto;
import faang.school.postservice.model.event.kafka.PostEventKafka;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface PostCacheService {

    void savePostToCache(PostCacheDto post);

    void addPostView(PostCacheDto post);

    void updateFeedsInCache(PostEventKafka event);

    CompletableFuture<Void> saveAllPostsToCache(List<PostCacheDto> posts);
}
