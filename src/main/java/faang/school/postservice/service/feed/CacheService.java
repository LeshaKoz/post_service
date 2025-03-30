package faang.school.postservice.service.feed;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.repository.RedisFeedRepository;
import faang.school.postservice.repository.RedisPostRepository;
import faang.school.postservice.repository.RedisUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    public void updatePost(PostResponseDto postDto) {

        savePost(postDto);
    }

    public void addUserToCache(Long authorId) {
        UserDto userDto = userServiceClient.getUser(authorId);
        log.info("addUserToCache userDto {}", userDto);
        redisUserRepository.save(userDto);
    }

    public Map<Long, UserDto> fetchUsers(Set<Long> userIds) {
        List<UserDto> userDtos = redisUserRepository.multiGet(userIds);

        log.info("fetchUsers userDtos {}", userDtos);

        Map<Long, UserDto> userMap = userDtos.stream()
                .collect(Collectors.toMap(
                        UserDto::id,
                        Function.identity()
                ));
        log.info("fetchUsers userMap {}", userMap);

        userIds.forEach(userId -> userMap.putIfAbsent(userId, null));
        processMissingUsers(userMap);
        log.info("fetchUsers updated userMap {}", userMap);
        return userMap;
    }

    private void processMissingUsers(Map<Long, UserDto> actualUserMap) {
        log.info("processMissingUsers  actualUserMap {}", actualUserMap);
        List<Long> missingUserIds = actualUserMap.entrySet().stream()
                .filter(entry -> entry.getValue() == null)
                .map(Map.Entry::getKey)
                .toList();

        log.info("processMissingUsers  missingUsersIds {}", missingUserIds);

        if (!missingUserIds.isEmpty()) {
            List<UserDto> missingUsers = getUsers(missingUserIds);
            saveUsers(missingUsers);
            missingUsers.forEach(userDto -> actualUserMap.put(userDto.id(), userDto));
        }
    }

    private List<UserDto> getUsers(List<Long> missingUserIds) {
        return userServiceClient.getUsersByIds(missingUserIds);
    }

    //@Async("feedExecutor")
    @Async
    public void saveUsers(List<UserDto> userDtos) {

        redisUserRepository.save(userDtos);
    }

    public List<PostResponseDto> fetchPosts(List<Long> postIds) {
        log.info("fetchPosts postIds {} ", postIds);
        List<PostResponseDto> postDtos = postIds.stream()
                .map(this::getPostFromCache)
                .filter(Optional::isPresent)
                .map(optionalPostDto -> {
                    PostResponseDto postDto = optionalPostDto.get();
                    log.info("postDto {}", postDto);
                    return postDto;
                })
                .toList();

        processMissingPosts(postIds, postDtos);
        log.info("fetchPosts updated postDtos {} ", postDtos);
        return postDtos;
    }

    private Optional<PostResponseDto> getPostFromCache(Long postId) {
        log.info("getPostFromCache postId {}", postId);
        return redisPostRepository.getPost(postId);
    }

    private void processMissingPosts(List<Long> expectedPostIds, List<PostResponseDto> postDtos) {
        log.info("processMissingPosts  expectedPostIds {}", expectedPostIds);
        Set<Long> actualPostIds = postDtos.stream().map(PostResponseDto::id).collect(Collectors.toSet());
        log.info("processMissingPosts  actualPostIds {}", actualPostIds);
        List<Long> missingPostIds = findMissingIds(actualPostIds, expectedPostIds);
        log.info("processMissingPosts  missingPostIds {}", missingPostIds);
        if (!missingPostIds.isEmpty()) {
            List<PostResponseDto> missingPostDtosFromDB = feedGetPostService.getPostDtosFromDB(missingPostIds);
            processNonexistentPosts(missingPostIds, missingPostDtosFromDB);
            postDtos.addAll(missingPostDtosFromDB);
        }
    }

    private void processNonexistentPosts(List<Long> expectedIds, List<PostResponseDto> actualPosts) {
        log.info("processNonexistentPosts  expectedIds {}", expectedIds);
        Set<Long> actualIds = actualPosts.stream().map(PostResponseDto::id).collect(Collectors.toSet());
        log.info("processNonexistentPosts  actualIds {}", actualIds);
        List<Long> missingIds = findMissingIds(actualIds, expectedIds);
        log.info("processNonexistentPosts  missingIds {}", missingIds);
        missingIds.forEach(this::handlePostDeletion);
    }

    private List<Long> findMissingIds(Set<Long> actualUserIds, List<Long> expectedUserIds) {
        return expectedUserIds.stream()
                .filter(id -> !actualUserIds.contains(id))
                .toList();
    }

    public void handlePostDeletion(Long postId) {
        redisPostRepository.deletePost(postId);
        redisFeedRepository.deletePostFromAllFeeds(postId);
    }
}
