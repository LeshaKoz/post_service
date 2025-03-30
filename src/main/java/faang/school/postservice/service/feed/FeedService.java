package faang.school.postservice.service.feed;

import faang.school.postservice.config.redis.CacheProperties;
import faang.school.postservice.dto.feed.FeedPostDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.repository.RedisFeedRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedService {
    private final RedisFeedRepository redisFeedRepository;
    private final CacheService cacheService;
    private final CacheProperties properties;
    private final FeedGetPostService feedGetPostService;

    public void addPostToFeed(List<Long> subscribersIds, Long postId, LocalDateTime publishedAt) {
        log.info("addPostToFeed subscribersIds {} Long postId {} publishedAt {} ", subscribersIds, postId, publishedAt);
        redisFeedRepository.addPost(subscribersIds, postId, publishedAt);
    }

    public void handlePostDeletion(Long postId) {
        log.info("handlePostDeletion postId {}", postId);
        cacheService.handlePostDeletion(postId);
        //redisFeedRepository.deletePostFromAllFeeds(postId);
    }

    public List<FeedPostDto> getFeed(Long userId, LocalDateTime lastSeenDate) {
        log.info("getFeed userId {} lastSeenDate {} ", userId, lastSeenDate);
        List<PostResponseDto> postDtos = constructPostsForFeed(userId, lastSeenDate);
        log.info("getFeed postDtos {} ", postDtos);
        Set<Long> userIds = prepareAuthorsIds(postDtos);
        log.info("getFeed userIds {} ", userIds);
        Map<Long, UserDto> usersMap = cacheService.fetchUsers(userIds);
        log.info("getFeed userMap {} ", usersMap);

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

        while (resultPostDtos.size() < properties.getPageSize()) {
            List<Long> postIds = redisFeedRepository.getPostIds(userId, currentLastSeenDate, properties.getPageSize());
            if (postIds.isEmpty()) {
                break;
            }
            List<PostResponseDto> postDtos = cacheService.fetchPosts(postIds);
            if (!postDtos.isEmpty()) {
                resultPostDtos.addAll(postDtos);
                currentLastSeenDate = getLastSeenDate(resultPostDtos);
            }
        }

        int quantityMissingPosts = properties.getPageSize() - resultPostDtos.size();
        if (quantityMissingPosts > 0) {
            List<PostResponseDto> missingPostsFromDB = feedGetPostService.retrievePosts(userId,
                    quantityMissingPosts, currentLastSeenDate);
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



}
