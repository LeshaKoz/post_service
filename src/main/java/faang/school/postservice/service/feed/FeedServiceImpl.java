package faang.school.postservice.service.feed;

import faang.school.postservice.dto.event.PostEventDto;
import faang.school.postservice.model.redis.FeedCache;
import faang.school.postservice.repository.redis.FeedCacheRepository;
import java.util.LinkedHashSet;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {

  @Value("${newsfeed.posts.limit}")
  private int postsLimit;

  @Value("${newsfeed.posts.clean}")
  private int postsToDrop;

  private final FeedCacheRepository feedCacheRepository;

  @Override
  //TODO optimistic lock, async processinf each follower's feed
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

}
