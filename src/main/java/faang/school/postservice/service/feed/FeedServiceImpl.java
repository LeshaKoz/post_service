package faang.school.postservice.service.feed;

import faang.school.postservice.dto.feed.FeedItemResponseDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.repository.feed.FeedRepository;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {

    private final PostService postService;
    private final FeedRepository feedRepository;
    //private final NewsFeedProperties newsFeedProperties;

    @Override
    public Set<FeedItemResponseDto> getFeed(long userId, int pageNum) {
        log.info("Get feed for user {}, page {}", userId, pageNum);
        Set<FeedItemResponseDto> result = feedRepository.feedItems(userId, pageNum);
        return result;
    }

    @Override
    public void processNewPost(Long postId, List<Long> followersIds) {

        PostResponseDto post = postService.getPost(postId);
        FeedItemResponseDto feedItem = FeedItemResponseDto.builder()
                //.postId(post.id())
                .postLikesCounter(0)
                //.publishedAt(post.publishedAt())
                .post(new FeedItemResponseDto.Post(post.id(), post.content(), post.publishedAt(), post.authorId()))
                .build();
        feedRepository.addPostToFollowersFeeds(followersIds, feedItem);
        log.info("Post {} processed", postId);
    }
}
