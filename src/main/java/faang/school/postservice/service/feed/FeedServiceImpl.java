package faang.school.postservice.service.feed;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.event.PostEventDto;
import faang.school.postservice.dto.feed.FeedDto;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.mapper.user.UserMapper;
import faang.school.postservice.model.redis.FeedCache;
import faang.school.postservice.model.redis.PostCache;
import faang.school.postservice.model.redis.UserCache;
import faang.school.postservice.repository.redis.FeedCacheRepository;
import faang.school.postservice.repository.redis.PostCacheRepository;
import faang.school.postservice.repository.redis.UserCacheRepository;
import faang.school.postservice.service.post.PostService;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {

  @Value("${newsfeed.feed.prefix}")
  private String feedPrefix;

  @Value("${newsfeed.feed.batch-size:1000}")
  private int batchSize;

  private final FeedCacheRepository feedCacheRepository;
  private final UserCacheRepository userCacheRepository;
  private final PostCacheRepository postCacheRepository;
  private final UserServiceClient userServiceClient;
  private final PostService postService;
  private final UserMapper userMapper;
  private final PostMapper postMapper;
  private final ExecutorService cachedThreadPool;

  @Override
  public void processPostEvent(PostEventDto dto) {
    List<Long> followers = dto.getFollowers();

    List<List<Long>> followersBatches = splitIntoBatches(followers);

    List<CompletableFuture<Void>> futures = followersBatches.stream()
        .map(
            batch -> CompletableFuture.runAsync(
                () -> batch.forEach(userId -> updateUserFeed(userId, dto)), cachedThreadPool))
        .toList();

    CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
        .thenRun(() -> log.info("feeds have been updated async"));
  }

  @Override
  public void updateUserFeed(Long userId, PostEventDto dto) {
    String feedKey = feedPrefix + userId;

    Runnable runnable = () -> {
      feedCacheRepository.addPost(userId, dto);
    };
    feedCacheRepository.executeWithOptimisticLock(runnable, feedKey);
  }

  private List<List<Long>> splitIntoBatches(List<Long> followers) {
    int totalSize = followers.size();
    int batchNumbs = (totalSize + batchSize - 1) / batchSize;
    List<List<Long>> batches = new ArrayList<>();
    for (int i = 0; i < batchNumbs; i++) {
      int start = i * batchSize;
      int end = Math.min(totalSize, (i + 1) * batchSize);
      batches.add(followers.subList(start, end));
    }
    return batches;
  }

  @Override
  public FeedDto getFeed(Long userId, Long previousPostId, int pageSize) {
    return mapToFeedDto(getCachedFeed(userId, previousPostId, pageSize));
  }

  private FeedCache getCachedFeed(Long userId, Long previousPostId, int pageSize) {
    List<Long> posts = feedCacheRepository.findPostsByUserId(userId, previousPostId, pageSize);
    return FeedCache.builder()
        .id(userId)
        .postsIds(new LinkedHashSet<>(posts))
        .build();
  }

  private FeedDto mapToFeedDto(FeedCache feedCache) {
    long userId = feedCache.getId();
    UserCache userCache = userCacheRepository.findById(userId)
        .orElseGet(() -> userCacheRepository.save(getUserFromDB(userId)));

    log.info("getting user: {}", userCache.getUsername());

    List<PostCache> posts = feedCache.getPostsIds().stream()
        .map(postId -> postCacheRepository.findById(postId)
            .orElseGet(() -> postCacheRepository.save(getPostFromDB(postId))))
        .toList();

    return FeedDto.builder()
        .user(userCache)
        .posts(posts)
        .build();
  }

  private UserCache getUserFromDB(Long userId) {
    return userMapper.toUserCache(userServiceClient.getUser(userId));
  }

  private PostCache getPostFromDB(Long postId) {
    PostCache postCache = postMapper.toPostCache(postService.findPostById(postId));
    postCache.setAuthorName(userServiceClient.getUser(postCache.getAuthorId()).getUsername());
    return postCache;
  }

}
