package faang.school.postservice.service;

import faang.school.postservice.repository.cache.RedisFeedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class FeedService {

    private final RedisFeedRepository redisFeedRepository;

    public void addPostToAuthorSubscribers(long postId, List<Long> subscribersId) {
        subscribersId.forEach(subscriberId -> addPostToSubscriberFeed(postId, subscriberId));
    }

    private void addPostToSubscriberFeed(long postId, long subscriberId) {
        Set<Long> postsId = redisFeedRepository.find(subscriberId);
        redisFeedRepository.checkMaxFeedSize(postsId, subscriberId);
        redisFeedRepository.add(subscriberId, postId);
    }
}
