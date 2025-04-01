package faang.school.postservice.service.feed;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.feed.FeedResponse;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.repository.post.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.Limit;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {

    private final UserServiceClient userServiceClient;
    private final RedisTemplate<String, String> redisTemplate;
    private final PostRepository postRepository;
    private final UserContext userContext;
    private final PostMapper postMapper;

    @Value("${feed.page-size:20}")
    private int pageSize;

    @Override
    public FeedResponse getNewsFeed(long userId, long after) {
        long currentUserId = userContext.getUserId();
        String feedKey = "user:feed:" + currentUserId;
        List<String> postIds = getPostIdsFromRedis(feedKey, String.valueOf(after));
        if (postIds.size() < pageSize) {
            List<String> additionalPostIds = loadMoreFromDatabase(
                    String.valueOf(currentUserId), String.valueOf(after), pageSize - postIds.size());
            postIds.addAll(additionalPostIds);
        }
        List<PostResponseDto> posts = enrichPostsData(postIds);
        return buildResponse(posts);
    }

    private List<String> getPostIdsFromRedis(String feedKey, String after) {
        Range<String> range = createRange(after);
        Set<String> postIds = redisTemplate.opsForZSet().reverseRangeByLex(feedKey, range, Limit.limit().count(pageSize));
        return postIds != null ?
                postIds.stream().map(Object::toString).collect(Collectors.toList()) :
                Collections.emptyList();
    }

    private List<String> loadMoreFromDatabase(String userId, String after, int limit) {
        List<PostResponseDto> posts = postRepository.findForUserFeed(Long.parseLong(userId),
                        after != null ? Instant.ofEpochMilli(Long.parseLong(after)) : null, limit).stream()
                .map(postMapper::toDto)
                .toList();
        cachePosts(posts);
        return posts.stream()
                .map(PostResponseDto::id)
                .map(String::valueOf)
                .toList();
    }

    private List<PostResponseDto> enrichPostsData(List<String> postIds) {
        return postIds.stream()
                .map(postId -> {
                    PostResponseDto postDto = getPostFromRedis(postId);
                    if (postDto == null) {
                        postDto = getPostFromDatabase(postId);
                    }
                    return postDto;
                })
                .toList();
    }

    private PostResponseDto getPostFromRedis(String postId) {
        String postKey = "post:" + postId;
        String authorKey = "user:" + postId;
        Map<Object, Object> postData = redisTemplate.opsForHash().entries(postKey);
        if (postData.isEmpty()) {
            return null;
        }
        Map<Object, Object> authorData = redisTemplate.opsForHash().entries(authorKey);
        if (authorData.isEmpty()) {
            return null;
        }
        return PostResponseDto.builder()
                .id(Long.parseLong(postId))
                .content((String) postData.get("content"))
                .createdAt(LocalDateTime.from(Instant.parse((String) postData.get("createdAt"))))
                .likesCount(Integer.parseInt((String) postData.get("likesCount")))
                .commentsCount(Integer.parseInt((String) postData.get("commentsCount")))
                .viewsCount(Integer.parseInt((String) postData.get("viewsCount")))
                .build();
    }

    private PostResponseDto getPostFromDatabase(String postId) {
        PostResponseDto post = postRepository.findById(Long.valueOf(postId))
                .map(postMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(postId));
        UserDto author = userServiceClient.getUser(post.authorId());
        cachePostAndAuthor(post, author);
        return post;
    }

    private void cachePosts(List<PostResponseDto> posts) {
        posts.forEach(post -> {
            UserDto author = userServiceClient.getUser(post.authorId());
            if (author != null) {
                cachePostAndAuthor(post, author);
            }
        });
    }

    private void cachePostAndAuthor(PostResponseDto post, UserDto author) {
        String postKey = "post:" + post.id();
        redisTemplate.opsForHash().putAll(postKey, Map.of(
                "content", post.content(),
                "createdAt", post.createdAt().toString(),
                "likesCount", String.valueOf(post.likesCount()),
                "commentsCount", String.valueOf(post.commentsCount()),
                "viewsCount", String.valueOf(post.viewsCount())
        ));
        redisTemplate.expire(postKey, 1, TimeUnit.DAYS);
        String authorKey = "user:" + author.getId();
        redisTemplate.opsForHash().putAll(authorKey, Map.of(
                "username", author.getUsername()
        ));
        redisTemplate.expire(authorKey, 1, TimeUnit.DAYS);
    }

    private FeedResponse buildResponse(List<PostResponseDto> posts) {
        FeedResponse response = FeedResponse.builder().build();
        response.setPosts(posts);
        if (!posts.isEmpty()) {
            response.setLastPostId(String.valueOf(posts.get(posts.size() - 1).id()));
            response.setHasMore(posts.size() >= pageSize);
        } else {
            response.setHasMore(false);
        }
        return response;
    }

    private Range<String> createRange(String after) {
        return after != null ?
                Range.from(Range.Bound.inclusive(after)).to(Range.Bound.unbounded()) :
                Range.unbounded();
    }
}
