package faang.school.postservice.service.feed;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.dto.user.UserResponseDto;
import faang.school.postservice.repository.RedisFeedRepository;
import faang.school.postservice.repository.RedisPostRepository;
import faang.school.postservice.repository.RedisUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CacheService {
    private final RedisFeedRepository redisFeedRepository;
    private final RedisUserRepository redisUserRepository;
    private final RedisPostRepository redisPostRepository;
    private final UserServiceClient userServiceClient;
    private final FeedGetPostService feedGetPostService;

    public void savePost(PostResponseDto postDto) {
        log.info("savePost postDto {}", postDto);
        redisPostRepository.addNewPost(postDto);
    }

    //@Async("feedExecutor")
    @Async
    public void savePosts(List<PostResponseDto> postDtos) {

        postDtos.forEach(this::savePost);
    }

    public void addUserToCache(Long authorId) {
        UserResponseDto userDto = userServiceClient.getUser(authorId);
        log.info("addUserToCache userDto {}", userDto);
        redisUserRepository.save(userDto);
    }

    private Optional<PostResponseDto> getPostFromCache(Long postId) {
        log.info("getPostFromCache postId {}", postId);
        return redisPostRepository.getPost(postId);
    }

    public void handlePostDeletion(Long postId) {
        redisPostRepository.deletePost(postId);
        redisFeedRepository.deletePostFromAllFeeds(postId);
    }
}
