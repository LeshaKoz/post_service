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
    private final PostMapper postMapper;

    @Transactional
    public List<PostResponseDto> retrievePosts(Long userId, int quantity, LocalDateTime lastSeenDate) {
        log.info("retrievePosts userId {} quantity {} lastSeenDate {} ", userId, quantity, lastSeenDate);
        List<Long> followeeIds = userServiceClient.getFolloweeIdsByFollowerId(userId);
        log.info("retrievePosts followeeIds {} ", followeeIds);
        List<Post> postsForFeed = postRepository.findPostsForFeed(followeeIds, lastSeenDate, quantity);
        log.info("retrievePosts postsForFeed {}", postsForFeed);
        /*List<Post> postsForFeed = postRepository.findPostsForFeed(followeeIds, lastSeenDate,
                PageRequest.of(0, quantity)); */
        return postsForFeed.stream()
                .map(postMapper::toPostResponseDto)
                .toList();
    }

    @Transactional
    public List<PostResponseDto> getPostDtosFromDB(List<Long> postIds) {
        log.info("getPostDtosFromDB  postIds {}", postIds);
        Iterable<Post> missingPosts = postRepository.findAllById(postIds);
        List<Post> posts = new ArrayList<>();
        missingPosts.forEach(posts::add);
        log.info("getPostDtosFromDB posts {}", posts);

        return posts.stream()
                .filter(post -> {
                    if (post.isDeleted()) {
                        log.info("Post with ID {} was found in DB but it was deleted", post.getId());
                        return false;
                    }
                    return true;
                })
                .map(postMapper::toPostResponseDto)
                .toList();
    }
}
