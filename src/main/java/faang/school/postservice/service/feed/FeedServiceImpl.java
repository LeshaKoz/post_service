package faang.school.postservice.service.feed;

import faang.school.postservice.broker.producer.PostProcessEventProducer;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.config.feed.NewsFeedProperties;
import faang.school.postservice.dto.feed.FeedItemDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.mapper.feed.FeedMapper;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.repository.feed.FeedRepository;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
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
    private final PostMapper postMapper;
    private final PostProcessEventProducer postProcessEventProducer;
    private final UserContext userContext;
    private final NewsFeedProperties newsFeedProperties;
    //private final NewsFeedProperties newsFeedProperties;

    @Override
    public Set<PostResponseDto> getFeed(long userId, int pageNum) {
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
                    //FeedItemPostDto feedItemPostDto = postMapper.toFeedItemPostDto(postResponseDto);
                    //FeedItemPostDto feedItemPostDto = postService.getPostForFeed(feedItemDto.postId());

                    //return feedMapper.toFeedResponseDto(feedItemDto, feedItemPostDto);
                    return postResponseDto;
                })
                .collect(Collectors.toSet());

    }

    @Override
    //@Async("asyncTaskExecutor")
    public void processNewPost(Long postId, List<Long> followersIds) {
        PostResponseDto postResponseDto = postService.getPostWithCache(postId);

        //feedRepository.addPostToFollowersFeeds(followersIds, post);
        int batchSize = newsFeedProperties.batchSize();

        List<List<Long>> batches = ListUtils.partition(followersIds, batchSize);

        if (CollectionUtils.isNotEmpty(batches)) {
            batches.forEach(batch -> {
                try {
                    // Проверяем, что batch не пуст
                    if (CollectionUtils.isNotEmpty(batch)) {
                        postProcessEventProducer.produceSubProcessPostEventAsync(
                                postResponseDto.authorId(),
                                postResponseDto,
                                batch
                        );
                    }
                } catch (Exception e) {
                    log.error("Failed to process batch: {}", batch, e);
                    // Можно добавить retry или dead-letter queue
                }
            });
        } else {
            log.warn("Batches list is empty or null");
        }
        log.info("Creation new post event processed. Post id {}", postId);
    }

    @Override
    //@Async("asyncTaskExecutor")
    public void subProcessNewPost(Long postId, List<Long> partFollowersIds) {
        PostResponseDto post = postService.getPostWithCache(postId);
        feedRepository.addPostToFollowersFeeds(partFollowersIds, post);
        log.info("Post {} partially processed", postId);
    }
}
