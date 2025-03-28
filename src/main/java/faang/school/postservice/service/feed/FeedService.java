package faang.school.postservice.service.feed;

import faang.school.postservice.dto.feed.FeedItemResponseDto;

import java.util.List;
import java.util.Set;

public interface FeedService {
    Set<FeedItemResponseDto> getFeed(long userId, int pageNum);

    void processNewPost(Long postId, List<Long> followersIds);
}
