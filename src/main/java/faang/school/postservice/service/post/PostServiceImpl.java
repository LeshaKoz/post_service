package faang.school.postservice.service.post;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.kafka.PostEvent;
import faang.school.postservice.dto.kafka.PostViewsEvent;
import faang.school.postservice.dto.post.PostRequestDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.post.Post;
import faang.school.postservice.properties.post.PostUnverifiedProperties;
import faang.school.postservice.properties.user.UserBanRedisProperties;
import faang.school.postservice.publisher.redis.RedisPublisher;
import faang.school.postservice.repository.post.PostRepository;
import faang.school.postservice.service.cache.AuthorCacheService;
import faang.school.postservice.service.cache.PostCacheService;
import faang.school.postservice.service.kafka.KafkaMessageService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
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
    private final PostCacheService postCacheService;
    private final AuthorCacheService authorCacheService;

    @Value("${kafka.post.topic}")
    private String postEventTopic;

    @Value("${kafka.post.views.topic}")
    private String postViewsTopic;

    @Override
    public PostResponseDto getPostById(long postId) {
        UserDto user = Optional.ofNullable(userServiceClient.getUser(userContext.getUserId()))
                .orElseThrow(() -> new EntityNotFoundException(String.format(
                        "User with id = %d not found", userContext.getUserId()
                )));
        Optional<PostResponseDto> postFromCache = postCacheService.getCachedPost(postId);
        if (postFromCache.isPresent()) {
            kafkaMessageService.sendMessage(postViewsTopic, new PostViewsEvent(user.getId(), postFromCache.get().id()));
            return postFromCache.get();
        }
        kafkaMessageService.sendMessage(postViewsTopic, new PostViewsEvent(user.getId(), postId));
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Post with id = %d not found", postId)));
        return postMapper.toDto(post);
    }

    @Override
    public List<PostResponseDto> getLatestPosts(List<String> authorIds, int limit) {
        if (authorIds == null || authorIds.isEmpty()) {
            return Collections.emptyList();
        }
        List<PostResponseDto> cachedPosts = postCacheService.getCachedPosts(authorIds, limit);
        if (cachedPosts.size() >= limit) {
            return cachedPosts.subList(0, limit);
        }
        List<PostResponseDto> dbPosts = getPostsFromDatabase(authorIds, limit - cachedPosts.size());
        List<PostResponseDto> result = new ArrayList<>(cachedPosts);
        result.addAll(dbPosts);
        postCacheService.cachePosts(dbPosts);
        return result.size() > limit ? result.subList(0, limit) : result;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public PostResponseDto createPost(PostRequestDto dto) {
        UserDto user = Optional.ofNullable(userServiceClient.getUser(userContext.getUserId()))
                .orElseThrow(() -> new EntityNotFoundException(String.format(
                        "User with id = %d not found", userContext.getUserId()
                )));
        Post savedPost = postRepository.save(buildPost(dto, user));
        return postMapper.toDto(savedPost);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public PostResponseDto publishPost(long postId) {
        UserDto user = Optional.ofNullable(userServiceClient.getUser(userContext.getUserId()))
                .orElseThrow(() -> new EntityNotFoundException(String.format(
                        "User with id = %d not found", userContext.getUserId()
                )));
        Post post = postRepository.findById(postId).orElseThrow(() -> new EntityNotFoundException(String.format(
                "Post with id = %d not found", postId
        )));
        checkAuthorAndPost(postId, post, user);
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        Post savedPost = postRepository.save(post);
        List<Long> followersIds = userServiceClient.getFollowersByUserId(user.getId()).stream()
                .map(UserDto::getId)
                .toList();
        kafkaMessageService.sendMessage(postEventTopic, new PostEvent(
                savedPost.getId(), savedPost.getAuthorId(), LocalDateTime.now(), followersIds, List.of()));
        postCacheService.cachePost(postMapper.toDto(savedPost));
        authorCacheService.cacheAuthor(savedPost.getId(), user);
        return postMapper.toDto(savedPost);
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
                .authorId(user.getId())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private void checkAuthorAndPost(long postId, Post post, UserDto user) {
        if (post.getAuthorId().longValue() != user.getId()) {
            throw new IllegalArgumentException(String.format("Access denied for user with id = %d to post id = %d",
                    user.getId(), postId));
        }
        if (post.isPublished()) {
            throw new IllegalArgumentException(String.format("Post with id = %d was published", postId));
        }
    }


    private List<PostResponseDto> getPostsFromDatabase(List<String> authorIds, int limit) {
        if (limit <= 0) {
            return Collections.emptyList();
        }
        List<Post> posts = postRepository.findLatestByAuthorIds(authorIds,
                PageRequest.of(0, limit, Sort.by("createdAt").descending()));
        return posts.stream()
                .map(postMapper::toDto)
                .toList();
    }
}
