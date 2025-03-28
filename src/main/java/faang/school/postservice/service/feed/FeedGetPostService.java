package faang.school.postservice.service.feed;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedGetPostService {
    private final UserServiceClient userServiceClient;
    private final PostRepository postRepository;
    //private final CacheService cacheService;
    private final PostMapper postMapper;

    @Transactional
    public List<PostResponseDto> retrievePosts(Long userId, int quantity, LocalDateTime lastSeenDate) {
        List<Long> followeeIds = userServiceClient.getFolloweeIdsByFollowerId(userId);
        List<Post> postsForFeed = postRepository.findPostsForFeed(followeeIds, lastSeenDate, quantity);
        return postsForFeed.stream()
                .map(postMapper::toPostResponseDto)
                .toList();
    }
}
