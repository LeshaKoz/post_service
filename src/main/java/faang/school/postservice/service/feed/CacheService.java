package faang.school.postservice.service.feed;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.RedisFeedRepository;
import faang.school.postservice.repository.RedisPostRepository;
import faang.school.postservice.repository.RedisUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CacheService {
    private final RedisFeedRepository redisFeedRepository;
    private final RedisUserRepository redisUserRepository;
    private final RedisPostRepository redisPostRepository;
    private final UserServiceClient userServiceClient;
    private final PostRepository postRepository;
    private final PostMapper postMapper;

    @Value("${spring.data.redis.cache.feed.showLastComments}")
    private int showLastComments;

    public void savePost(PostResponseDto postDto) {
        redisPostRepository.addNewPost(postDto);
    }

    @Async("feedExecutor")
    public void savePosts(List<PostResponseDto> postDtos) {
        postDtos.forEach(this::savePost);
    }

    public void updatePost(PostResponseDto postDto) {
        savePost(postDto);
    }

    public void addUserToCache(Long authorId) {
        UserDto userDto = userServiceClient.getUser(authorId);
        redisUserRepository.save(userDto);
    }

    public Map<Long, UserDto> fetchUsers(Set<Long> userIds) {
        List<UserDto> userDtos = redisUserRepository.multiGet(userIds);

        Map<Long, UserDto> userMap = userDtos.stream()
                .collect(Collectors.toMap(
                        UserDto::id,
                        Function.identity()
                ));

        userIds.forEach(userId -> userMap.putIfAbsent(userId, null));

        processMissingUsers(userMap);

        return userMap;
    }

    private void processMissingUsers(Map<Long, UserDto> actualUserMap) {
        List<Long> missingUserIds = actualUserMap.entrySet().stream()
                .filter(entry -> entry.getValue() == null)
                .map(Map.Entry::getKey)
                .toList();

        if (!missingUserIds.isEmpty()) {
            List<UserDto> missingUsers = getUsersFromDB(missingUserIds);
            saveUsers(missingUsers);
            missingUsers.forEach(userDto -> actualUserMap.put(userDto.id(), userDto));
        }
    }

    private List<UserDto> getUsersFromDB(List<Long> missingUserIds) {
        return userServiceClient.getUsersByIds(missingUserIds);
    }

    @Async("feedExecutor")
    public void saveUsers(List<UserDto> userDtos) {
        redisUserRepository.save(userDtos);
    }

    public List<PostResponseDto> fetchPosts(List<Long> postIds) {
        List<PostResponseDto> postDtos = postIds.stream()
                .map(this::getPostFromCache)
                .filter(Optional::isPresent)
                .map(optionalPostDto -> {
                    PostResponseDto postDto = optionalPostDto.get();
                    updatePostCounters(postDto);
                    return postDto;
                })
                .toList();

        processMissingPosts(postIds, postDtos);

        return postDtos;
    }

    private Optional<PostResponseDto> getPostFromCache(Long postId) {
        return redisPostRepository.getPost(postId);
    }

    private void updatePostCounters(PostResponseDto postDto) {
        Long postId = postDto.id();
/*
        Long likesDelta = redisPostRepository.getLikesCounter(postId);
        Long commentsDelta = redisPostRepository.getCommentsCounter(postId);

        long totalLikes = postDto.getLikesCount() + (likesDelta != null ? likesDelta : 0);
        long totalComments = postDto.getCommentsCount() + (commentsDelta != null ? commentsDelta : 0);

        postDto.setLikesCount(Math.max(totalLikes, 0));
        postDto.setCommentsCount(Math.max(totalComments, 0));*/
    }

    private void processMissingPosts(List<Long> expectedPostIds, List<PostResponseDto> postDtos) {
        Set<Long> actualPostIds = postDtos.stream().map(PostResponseDto::id).collect(Collectors.toSet());
        List<Long> missingPostIds = findMissingIds(actualPostIds, expectedPostIds);

        if (!missingPostIds.isEmpty()) {
            List<PostResponseDto> missingPostDtosFromDB = getPostDtosFromDB(missingPostIds);
            processNonexistentPosts(missingPostIds, missingPostDtosFromDB);
            postDtos.addAll(missingPostDtosFromDB);
        }
    }

    private void processNonexistentPosts(List<Long> expectedIds, List<PostResponseDto> actualPosts) {
        Set<Long> actualIds = actualPosts.stream().map(PostDto::id).collect(Collectors.toSet());
        List<Long> missingIds = findMissingIds(actualIds, expectedIds);
        missingIds.forEach(id -> {
            handlePostDeletion(id);
            redisFeedRepository.deletePostFromAllFeeds(id);
        });
    }

    private List<Long> findMissingIds(Set<Long> actualUserIds, List<Long> expectedUserIds) {
        return expectedUserIds.stream()
                .filter(id -> !actualUserIds.contains(id))
                .toList();
    }

    @Transactional
    public List<PostResponseDto> getPostDtosFromDB(List<Long> postsIds) {
        Iterable<Post> missingPosts = postRepository.findAllById(postsIds);
        List<Post> posts = new ArrayList<>();
        missingPosts.forEach(posts::add);

        return posts.stream()
                .filter(post -> {
                    if (post.isDeleted()) {
                        log.info("Post with ID {} was found in DB but it was deleted", post.getId());
                        handlePostDeletion(post.getId());
                        return false;
                    }
                    return true;
                })
                .map(postMapper::toPostResponseDto)
                .toList();
    }

    public void handlePostDeletion(Long postId) {
        redisPostRepository.deletePost(postId);
        redisFeedRepository.deletePostFromAllFeeds(postId);
    }
}
