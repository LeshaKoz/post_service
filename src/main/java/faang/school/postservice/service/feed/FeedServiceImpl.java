package faang.school.postservice.service.feed;

import faang.school.postservice.dto.feed.FeedItemDto;
import faang.school.postservice.dto.feed.FeedItemResponseDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.mapper.feed.FeedMapper;
import faang.school.postservice.repository.feed.FeedRepository;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {

    private final PostService postService;
    private final FeedRepository feedRepository;
    private final FeedMapper feedMapper;
    //private final NewsFeedProperties newsFeedProperties;

    @Override
    public Set<FeedItemResponseDto> getFeed(long userId, int pageNum) {
        log.info("Get feed for user {}, page {}", userId, pageNum);

        Set<FeedItemDto> feedItems = feedRepository.feedItems(userId, pageNum);

/*        Iterator <FeedItemDto> iterator = feedItems.iterator();
        while(iterator.hasNext()) {
            FeedItemDto feedItemDto = iterator.next();
            PostResponseDto postResponseDto = postService.getPostWithCache(feedItemDto.postId());
            FeedItemResponseDto feedItemResponseDto = feedMapper.toFeedResponseDto(feedItemDto, postResponseDto);
        }*/
        return feedItems.stream()
                .map(feedItemDto -> {
                    PostResponseDto postResponseDto = postService.getPostWithCache(feedItemDto.postId());
                    return feedMapper.toFeedResponseDto(feedItemDto, postResponseDto);
                })
                .collect(Collectors.toSet());

    }

    @Override
    //@Async("asyncTaskExecutor")
    public void processNewPost(Long postId, List<Long> followersIds) {

        PostResponseDto post = postService.getPostWithCache(postId);

        feedRepository.addPostToFollowersFeeds(followersIds, post);
        log.info("Post {} processed", postId);
    }
}
