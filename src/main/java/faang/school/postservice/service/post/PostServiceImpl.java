package faang.school.postservice.service.post;

import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.post.Post;
import faang.school.postservice.properties.post.PostUnverifiedProperties;
import faang.school.postservice.properties.user.UserBanRedisProperties;
import faang.school.postservice.repository.post.PostRepository;
import faang.school.postservice.publisher.redis.RedisPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final UserBanRedisProperties userBanRedisProperties;
    private final PostUnverifiedProperties postUnverifiedProperties;
    private final RedisPublisher redisPublisher;

    @Cacheable(key = "#hashtag", value = "postsByHashtag")
    @Override
    public List<PostResponseDto> getPostsByHashtag(String hashtag) {
        log.info("Get posts by hashtag");
        return postRepository.findByHashtag(hashtag)
                .stream()
                .map(postMapper::toDto)
                .toList();
    }

    @Override
    public void banUsersWithManyUnverifiedPosts() {
        Map<Long, List<Post>> unverifiedPostsByUsers = postRepository.findByVerified(false).stream()
                .collect(groupingBy(Post::getAuthorId));
        unverifiedPostsByUsers.entrySet().stream()
                .filter(entry -> entry.getValue().size() > postUnverifiedProperties.getMax())
                .forEach(entry -> {
                    long authorId = entry.getKey();
                    redisPublisher.publish(userBanRedisProperties.getChannel(), String.valueOf(authorId));
                    log.info("Sent ban request for author: {}", authorId);
                });
    }
}
