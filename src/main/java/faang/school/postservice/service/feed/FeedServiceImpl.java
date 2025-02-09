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
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {

  @Value("${newsfeed.posts.limit}")
  private int postsLimit;

  @Value("${newsfeed.posts.clean}")
  private int postsToDrop;

  private final FeedCacheRepository feedCacheRepository;
  private final UserCacheRepository userCacheRepository;
  private final PostCacheRepository postCacheRepository;
  private final UserServiceClient userServiceClient;
  private final PostService postService;
  private final UserMapper userMapper;
  private final PostMapper postMapper;

  //TODO async processing each follower's feed
  @Override
  @Retryable(retryFor = OptimisticLockingFailureException.class, backoff = @Backoff(delay = 3000L))
  public void processPostEvent(PostEventDto dto) {
    List<Long> followers = dto.getFollowers(); // list of users to update theirs feeds

    Long postId = dto.getPosId();

    followers.forEach(user -> updateUserFeed(user, postId));

//    CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).thenRun(() -> ...);
  }

  @Override
  public void updateUserFeed(Long userId, Long postId) {
    FeedCache testFeed = getUserFeed(userId);

    LinkedHashSet<Long> posts = testFeed.getPostsIds();

    testFeed.addPost(postId);

    if (posts.size() > postsLimit) {
      for (int i = 0; i < postsToDrop; i++) {
        posts.remove(posts.iterator().next());
      }
    }
    feedCacheRepository.save(testFeed);
  }

  @Override
  public FeedCache getUserFeed(Long userId) {
    return feedCacheRepository.findById(userId)
        .orElseGet(() ->
            FeedCache.builder()
                .id(userId)
                .postsIds(new LinkedHashSet<>())
                .build()
        );
  }

  @Override
  public FeedCache getUserFeed(Long userId, int previousPostId, int pageSize) {
    FeedCache feed = feedCacheRepository.findById(userId)
        .orElseGet(() ->
            FeedCache.builder()
                .id(userId)
                .postsIds(new LinkedHashSet<>())
                .build()
        );

    LinkedHashSet<Long> postsIds = feed.getPostsIds();

    if (!postsIds.isEmpty()) {
      var nextPosts = postsIds.stream()
//          .limit()
          .skip(Math.max(0, postsIds.size() - pageSize))
          .sorted(Comparator.reverseOrder())
          .collect(Collectors.toCollection(LinkedHashSet::new));

      feed.setPostsIds(nextPosts);
    }
    return feed;
  }

  private FeedDto mapToFeedDto(FeedCache feedCache) {

    long userId = feedCache.getId();
    //может быть ситуация, что юзера нет в редисе, если он не автор или давно не постил
    // Редис упал (при правильном масштабировании мало вероятно, но всме-таки
    // идем тогда в БД
    UserCache userCache = userCacheRepository.findById(userId)
        .orElse(userMapper.toUserCache(userServiceClient.getUser(userId)));

    // посты есть в Редис, но он может упасть и тогда все пусто, тогда надо в БД идти
    List<PostCache> posts = feedCache.getPostsIds().stream()
        .map(id -> postCacheRepository.findById(id)
            .orElse(postMapper.toPostCache(postService.findPostById(userId))))
        .toList();

    //TODO выносить в отдельные методы + исключения, когда и в БД не нашли
    //и в контроллер выдвать dto нормальную (пока данные из кэша)

    return FeedDto.builder()
        .user(userCache)
        .posts(posts)
        .build();
  }

}
