package faang.school.postservice.service.feed;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.feed.FeedDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.kafka.producer.KafkaEventProducer;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class FeedHeatService {

    @Value("${spring.data.redis.heat.max-posts-in-feed}")
    private int maxPostsInHeatFeed;

    private final KafkaEventProducer kafkaEventProducer;
    private final AuthorCacheService authorCacheService;
    private final UserServiceClient userServiceClient;
    private final PostService postService;

    public void sendHeatEvents() {
        List<UserDto> allUsers = userServiceClient.getAllUsers();
        authorCacheService.saveAllAuthorsInCache(allUsers);

        List<FeedDto> feedEvents = generateFeedsForAllUserFollowers(allUsers);
        sendFeedHeatEvents(feedEvents);

        List<PostDto> postEvents = generatePostEvents(feedEvents);
        sendPostHeatEvents(postEvents);
    }

    private void sendPostHeatEvents(List<PostDto> postEvents) {
        for (PostDto post : postEvents) {
            kafkaEventProducer.sendPostHeatEvent(post);
        }
    }

    private void sendFeedHeatEvents(List<FeedDto> feedEvents) {
        for (FeedDto feed : feedEvents) {
            kafkaEventProducer.sendFeedHeatEvent(feed);
        }
    }

    private List<FeedDto> generateFeedsForAllUserFollowers(List<UserDto> allUsersInOurSystem) {
        return allUsersInOurSystem.parallelStream()
                .map(follower -> {
                    Long followerId = follower.getId();
                    List<UserDto> bloggers = userServiceClient.getUsersByIds(follower.getFollowees());

                    List<Long> allBloggersPostIds = bloggers.stream()
                            .flatMap(blogger -> blogger.getPosts().stream())
                            .limit(maxPostsInHeatFeed)
                            .collect(Collectors.toList());

                    List<PostDto> allBloggersPosts = postService.getPostsByIds(allBloggersPostIds);

                    return new FeedDto(followerId, allBloggersPosts);
                })
                .collect(Collectors.toList());
    }

    private List<PostDto> generatePostEvents(List<FeedDto> feedDtos) {
        return feedDtos.stream()
                .flatMap(feedDto -> feedDto.posts().stream())
                .distinct()
                .collect(Collectors.toList());
    }
}