package faang.school.postservice.service.feed;

import faang.school.postservice.dto.feed.FeedItemResponseDto;
import faang.school.postservice.dto.post.PostResponseDto;

import java.util.List;
import java.util.Set;

public interface FeedService {
    Set<PostResponseDto>  getFeed(long userId, int pageNum);

    void processNewPost(Long postId, List<Long> followersIds);
}
