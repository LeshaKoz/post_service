package faang.school.postservice.service.post;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.event.CommentEvent;
import faang.school.postservice.dto.kafka.PostPublishedEvent;
import faang.school.postservice.dto.post.PostRequestDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.post.Post;
import faang.school.postservice.properties.post.PostUnverifiedProperties;
import faang.school.postservice.properties.user.UserBanRedisProperties;
import faang.school.postservice.repository.post.PostRepository;
import faang.school.postservice.publisher.redis.RedisPublisher;
import faang.school.postservice.service.kafka.KafkaMessageService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    private final UserContext userContext;
    private final UserServiceClient userServiceClient;
    private final KafkaMessageService kafkaMessageService;

    @Value("${kafka.post.topic}")
    private String topic;

    @Cacheable(key = "#postId", value = "posts")
    @Override
    public PostResponseDto getPostById(long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new EntityNotFoundException(String.format(
                "Post with id = %d not found", postId
        )));
        return postMapper.toDto(post);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public PostResponseDto createPost(PostRequestDto dto) {
        UserDto user = Optional.ofNullable(userServiceClient.getUser(userContext.getUserId()))
                .orElseThrow(() -> new EntityNotFoundException(String.format(
                        "User with id = %d not found", userContext.getUserId()
                )));
        List<Long> followersIds = userServiceClient.getFollowersByUserId(user.id()).stream()
                .map(UserDto::id)
                .toList();
        kafkaMessageService.sendMessage(topic, new PostPublishedEvent(followersIds));
        return postMapper.toDto(postRepository.save(buildPost(dto, user)));
    }


    @Transactional(rollbackFor = Exception.class)
    @CachePut(key = "#postId", value = "posts", condition = "#postId != null")
    @Override
    public PostResponseDto publishPost(long postId) {
        UserDto user = Optional.ofNullable(userServiceClient.getUser(userContext.getUserId()))
                .orElseThrow(() -> new EntityNotFoundException(String.format(
                        "User with id = %d not found", userContext.getUserId()
                )));
        Post post = postRepository.findById(postId).orElseThrow(() -> new EntityNotFoundException(String.format(
                "Post with id = %d not found", postId
        )));
        if (post.getAuthorId().longValue() != user.id()) {
            throw new IllegalArgumentException(String.format("Access denied for user with id = %d to post id = %d",
                    user.id(), postId));
        }
        if (post.isPublished()) {
            throw new IllegalArgumentException(String.format("Post with id = %d was published", postId));
        }
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        Post savedPost = postRepository.save(post);
        saveAuthorToCache(user);
        return postMapper.toDto(savedPost);
    }

    @CachePut(key = "#id", value = "authors")
    private void saveAuthorToCache(UserDto user) {

    }

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

    private Post buildPost(PostRequestDto dto, UserDto user) {
        return Post.builder()
                .content(dto.content())
                .authorId(user.id())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
