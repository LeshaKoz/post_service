package faang.school.postservice.service.news_feed;

import faang.school.postservice.dto.feed.FeedResponseDto;
import faang.school.postservice.dto.post.PostRedisDto;
import faang.school.postservice.dto.user.UserRedisDto;
import faang.school.postservice.mapper.FeedMapper;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.redis.RedisFeedRepository;
import faang.school.postservice.repository.redis.RedisPostRepository;
import faang.school.postservice.repository.redis.RedisUserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class FeedService {
    private final RedisFeedRepository redisFeedRepository;
    private final RedisUserRepository redisUserRepository;
    private final RedisPostRepository redisPostRepository;
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final FeedMapper feedMapper;
    private static final int PAGE_SIZE = 20;

    public List<FeedResponseDto> getUserFeed(Long userId, Long afterPostId) {
        if (redisUserRepository.checkUserExist(userId)) {
            throw new EntityNotFoundException("user not found " + userId);
        }

        List<Long> feedPostIds = redisFeedRepository.getFeedPostIds(userId, afterPostId, PAGE_SIZE);

        List<PostRedisDto> posts = redisPostRepository.getPosts(feedPostIds);
        int postSize = posts.size();

        if (postSize < PAGE_SIZE) {
            List<Long> missingPost = posts.stream()
                .map(PostRedisDto::getId)
                .filter(id -> !feedPostIds.contains(id))
                .toList();

            List<Post> allById = postRepository.findAllById(missingPost);
            List<PostRedisDto> postRedisDto = postMapper.toRedisEntityList(allById);
            posts.addAll(postRedisDto);
            redisPostRepository.cachePosts(postRedisDto);
        }

        List<Long> authorIds = posts.stream()
            .map(PostRedisDto::getAuthorId)
            .toList();

        List<UserRedisDto> users = redisUserRepository.getUsers(authorIds);

        Map<Long, UserRedisDto> userMap = users.stream()
            .collect(Collectors.toMap(UserRedisDto::id, user -> user));

        return posts.stream()
            .map(post -> feedMapper.toFeedResponseDto(post, userMap.get(post.getAuthorId())))
            .collect(Collectors.toList());
    }
}
