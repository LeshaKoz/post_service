package faang.school.postservice.service.feed;

import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.model.event.PostEvent;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.redis.RedisFeedRepository;
import faang.school.postservice.repository.redis.RedisPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
public class FeedService {
    private final static int POST_LIMIT = 20;

    private final RedisPostRepository redisPostRepository;
    private final RedisFeedRepository redisFeedRepository;
    private final PostRepository postRepository;

    public Set<PostEvent> getUserFeed(Long postId, Long userId) {
        if (postId == null) {
            //достаем из редиса последние 20
            Set<Long> postIds = redisFeedRepository.findById(userId).orElseThrow(
                    () -> new IllegalStateException(format("User %s has no news feed", userId))
            ).getPostsId().stream()
                    .limit(POST_LIMIT)
                    .collect(Collectors.toSet());
            //Если нет 20, то надо идти бд

            if (postIds.isEmpty()) {
                Set<PostEvent> postFromDb = postRepository.f
            }
        } else {
            //достаем из редиса следующую за postId пачку постов
        }
        return List.of(new PostResponseDto());
    }
}
