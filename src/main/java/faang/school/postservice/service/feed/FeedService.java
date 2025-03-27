package faang.school.postservice.service.feed;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.feed.FeedPostDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.RedisFeedRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedService {
    private final RedisFeedRepository redisFeedRepository;
    private final CacheService cacheService;
    private final UserServiceClient userServiceClient;
    private final PostRepository postRepository;
    private final PostMapper postMapper;

    @Value("${spring.data.redis.cache.feed.pageSize}")
    private int pageSize;

    public void addPostToFeed(List<Long> subscribersIds, Long postId, LocalDateTime publishedAt) {
        redisFeedRepository.addPost(subscribersIds, postId, publishedAt);
    }

    public void handlePostDeletion(Long postId) {
        cacheService.handlePostDeletion(postId);
        redisFeedRepository.deletePostFromAllFeeds(postId);
    }

    public List<FeedPostDto> getFeed(Long userId, LocalDateTime lastSeenDate) {
        List<PostResponseDto> postDtos = constructPostsForFeed(userId, lastSeenDate);
        Set<Long> userIds = prepareAuthorsIds(postDtos);
        Map<Long, UserDto> usersMap = cacheService.fetchUsers(userIds);

        return assembleFeedPosts(postDtos, usersMap);
    }

    private Set<Long> prepareAuthorsIds(List<PostResponseDto> postDtos) {
        return postDtos.stream()
                .map(PostResponseDto::authorId)
                .collect(Collectors.toSet());
    }

    private List<PostResponseDto> constructPostsForFeed(Long userId, LocalDateTime lastSeenDate) {
        List<PostResponseDto> resultPostDtos = new ArrayList<>();
        LocalDateTime currentLastSeenDate = lastSeenDate;

        while (resultPostDtos.size() < pageSize) {
            List<Long> postIds = redisFeedRepository.getPostIds(userId, currentLastSeenDate, pageSize);
            if (postIds.isEmpty()) {
                break;
            }
            List<PostResponseDto> postDtos = cacheService.fetchPosts(postIds);
            if (!postDtos.isEmpty()) {
                resultPostDtos.addAll(postDtos);
                currentLastSeenDate = getLastSeenDate(resultPostDtos);
            }
        }

        int quantityMissingPosts = pageSize - resultPostDtos.size();
        if (quantityMissingPosts > 0) {
            List<PostResponseDto> missingPostsFromDB = fetchPostsFromDB(userId, quantityMissingPosts, currentLastSeenDate);
            resultPostDtos.addAll(missingPostsFromDB);
        }

        return resultPostDtos;
    }

    private LocalDateTime getLastSeenDate(List<PostResponseDto> resultPostDtos) {
        return resultPostDtos.stream()
                .map(PostResponseDto::publishedAt)
                .min(LocalDateTime::compareTo)
                .orElse(null);
    }

    private List<FeedPostDto> assembleFeedPosts(
            List<PostResponseDto> postDtos,
            Map<Long, UserDto> usersMap) {

        return postDtos.stream()
                .sorted(Comparator.comparing(PostResponseDto::publishedAt).reversed())
                .flatMap(postDto -> {
                    UserDto postAuthor = usersMap.get(postDto.authorId());
                    if (postAuthor == null) {
                        log.warn("Can't create FeedPostDto, because author not found for postId: {} " +
                                "with authorId: {}. Skipping this post.", postDto.id(), postDto.authorId());
                        return Stream.empty();
                    } else {
                        FeedPostDto feedPostDto = FeedPostDto.builder()
                                .postDto(postDto)
                                .author(postAuthor)
                                .build();
                        return Stream.of(feedPostDto);
                    }
                })
                .toList();
    }


    @Transactional
    public List<PostResponseDto> fetchPostsFromDB(Long userId, int quantity, LocalDateTime lastSeenDate) {
        List<Long> followeeIds = userServiceClient.getFolloweeIdsByFollowerId(userId);
        List<Post> postsForFeed = postRepository.findPostsForFeed(followeeIds, lastSeenDate, quantity);
        return postsForFeed.stream()
                .map(postMapper::toPostResponseDto)
                .toList();
    }
}
