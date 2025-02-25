package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.thread.pool.ThreadPoolConfig;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.PostForNewsFeedDto;
import faang.school.postservice.dto.post.UpdatePostDto;
import faang.school.postservice.dto.sightengine.textAnalysis.ModerationClasses;
import faang.school.postservice.dto.sightengine.textAnalysis.TextAnalysisResponse;
import faang.school.postservice.dto.user.UserForNewsFeedDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.mapper.UserMapper;
import faang.school.postservice.message.event.PostEvent;
import faang.school.postservice.message.producer.KafkaPostProducer;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.cache.PostCache;
import faang.school.postservice.model.cache.UserCache;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.cache.RedisUserRepository;
import faang.school.postservice.service.moderation.ModerationDictionary;
import faang.school.postservice.service.moderation.sightengine.ModerationVerifierFactory;
import faang.school.postservice.service.moderation.sightengine.SightEngineReactiveClient;
import faang.school.postservice.service.redis.RedisPostService;
import faang.school.postservice.service.redis.RedisUserService;
import faang.school.postservice.validator.PostValidator;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;
    private final PostValidator validator;
    private final SightEngineReactiveClient sightEngineReactiveClient;
    private final ModerationDictionary moderationDictionary;
    private final ModerationVerifierFactory moderationVerifierFactory;
    private final RedisPostService redisPostService;
    private final RedisUserService redisUserService;
    private final UserMapper userMapper;
    private final KafkaPostProducer kafkaPostProducer;

    @Transactional
    public PostDto createPost(PostDto postDto) {
        log.info("Request to create a new post: {}", postDto);
        validator.validateAuthorPostCreation(postDto);
        validateUserOrProjectExist(postDto);

        Post post = postMapper.toEntity(postDto);
        post = postRepository.save(post);
        log.info("Post with id {} created: {}", post.getId(), post);

        kafkaPostProducer.publish(createPostEvent(post));

        return postMapper.toDto(post);
    }

    @Transactional
    public PostDto publishPost(long id) {
        Post post = findPostById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post with id %s not found".formatted(id)));
        log.debug("Request to publish a post: {}", post);
        if (post.isPublished()) {
            return postMapper.toDto(post);
        }
        validateThatPostDeleted(post);

        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());

        savePublishedPostToCache(post);
        saveAuthorOfThePostToCache(post);

        log.info("Post {} published", post);
        return postMapper.toDto(post);
    }

    @Transactional
    public PostDto updatePost(long id, UpdatePostDto updatePostDto) {
        Post post = findPostById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post with id %s not found".formatted(id)));
        log.info("Request to update a post: {}", post);
        validateThatPostDeleted(post);

        post.setContent(updatePostDto.content());
        log.info("Post with id {} has been updated", post.getId());
        return postMapper.toDto(post);
    }

    @Transactional
    public PostDto deletePost(long id) {
        Post post = findPostById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post with id %s not found".formatted(id)));
        log.info("Request to delete a post: {}", post);
        validateThatPostDeleted(post);

        post.setDeleted(true);
        log.info("Post with id {} has been deleted", id);
        return postMapper.toDto(post);
    }

    public List<PostDto> getAllDraftNotDeletedPostsByUserId(long userId) {
        List<Post> posts = postRepository.findByAuthorId(userId);
        log.info("Get all draft not deleted posts by user {}", userId);
        return getDraftNotDeletedPostsSortedByCreatedAt(posts);
    }

    public List<PostDto> getAllDraftNotDeletedPostsByProjectId(long projectId) {
        List<Post> posts = postRepository.findByProjectId(projectId);
        log.info("Get all draft not deleted posts by project {}", projectId);
        return getDraftNotDeletedPostsSortedByCreatedAt(posts);
    }

    public List<PostDto> getAllPublishedNotDeletedPostsByUserId(long userId) {
        List<Post> posts = postRepository.findByAuthorId(userId);
        log.info("Get all published not deleted posts by user {}", userId);
        return getPublishedNotDeletedPostsSortedByPublishedAt(posts);
    }

    public List<PostDto> getAllPublishedNotDeletedPostsByProjectId(long projectId) {
        List<Post> posts = postRepository.findByProjectId(projectId);
        log.info("Get all published not deleted posts by project {}", projectId);
        return getPublishedNotDeletedPostsSortedByPublishedAt(posts);
    }

    private List<PostDto> getDraftNotDeletedPostsSortedByCreatedAt(List<Post> posts) {
        return posts.stream()
                .filter(post -> !post.isDeleted() && !post.isPublished())
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .map(postMapper::toDto)
                .toList();
    }

    private List<PostDto> getPublishedNotDeletedPostsSortedByPublishedAt(List<Post> posts) {
        return posts.stream()
                .filter(post -> !post.isDeleted() && post.isPublished())
                .sorted(Comparator.comparing(Post::getPublishedAt).reversed())
                .map(postMapper::toDto)
                .toList();
    }

    public void addCommentToPost(Post post, Comment comment) {
        post.addComment(comment);
    }

    private void validateUserOrProjectExist(PostDto postDto) {
        try {
            if (postDto.projectId() != null) {
                projectServiceClient.getProject(postDto.projectId());
            } else {
                userServiceClient.getUser(postDto.authorId());
            }
        } catch (FeignException e) {
            log.error("Error checking the existence of a user or project {}", postDto, e);
            throw new EntityNotFoundException("The author has not been found " + e.getMessage());
        }
    }

    public Optional<Post> findPostById(long id) {
        return postRepository.findById(id);
    }

    public PostDto getPostDtoById(long id) {
        return postMapper.toDto(findPostById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post with id %s not found".formatted(id))));
    }

    public Post getPostById(long postId) {
        log.debug("start searching post by id {}", postId);
        return postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post is not found"));

    }

    private void validateThatPostDeleted(Post post) {
        if (post.isDeleted()) {
            throw new DataValidationException("It is not possible to update a deleted post");
        }
    }

    public boolean isPostNotExist(long postId) {
        log.debug("start searching for existence post with id {}", postId);
        return !postRepository.existsById(postId);
    }

    public List<Post> findNotReviewedPosts() {
        log.info("Trying to get posts that have not been reviewed");
        return postRepository.findByVerifiedDateIsNull();
    }

    @Async(value = ThreadPoolConfig.VERIFICATION_POOL_BEAN_NAME)
    public void verifyPostsAsync(List<Post> posts) {
        posts.forEach(post -> {
            log.info("start verifying post with id {}", post.getId());
            sightEngineReactiveClient.analyzeText(post.getContent())
                    .subscribe(
                            response -> {
                                log.debug("Response received! Verifying post with id {}", post.getId());
                                boolean verified = postIsVerified(response, post);
                                post.setVerified(verified);
                                post.setVerifiedDate(LocalDateTime.now());
                                postRepository.save(post);
                            },
                            ex -> {
                                log.error("Text analyzer client return error {}. Post with id {}",
                                        ex.getMessage(), post.getId(), ex);
                                boolean verified = moderationDictionary.hasNoRestrictedWords(post.getContent());
                                post.setVerified(verified);
                                post.setVerifiedDate(LocalDateTime.now());
                                postRepository.save(post);
                            }
                    );
        });
    }

    private boolean postIsVerified(TextAnalysisResponse textAnalysisResponse, Post post) {
        if (textAnalysisResponse == null) {
            log.warn("Text analysis response is null. Analyse with dictionary");
            return moderationDictionary.hasNoRestrictedWords(post.getContent());
        }
        if (textAnalysisResponse.getModerationClasses() == null) {
            log.warn("Moderation classes is null. Analyse with dictionary");
            return moderationDictionary.hasNoRestrictedWords(post.getContent());
        }

        log.debug("Start analysing response");
        ModerationClasses moderationClasses = textAnalysisResponse.getModerationClasses();
        return moderationVerifierFactory.create()
                .sexual(moderationClasses.getSexual())
                .discriminatory(moderationClasses.getDiscriminatory())
                .insulting(moderationClasses.getInsulting())
                .violent(moderationClasses.getViolent())
                .toxic(moderationClasses.getToxic())
                .verify();
    }

    private void savePublishedPostToCache(Post post) {
        if (post.isPublished()) {
            log.debug("Saving published post to cache: {}", post.getId());
            PostCache postCache = postMapper.toCache(post);
            redisPostService.savePostToCache(postCache);
        }
    }

    private void saveAuthorOfThePostToCache(Post post) {
        if (post.isPublished()) {
            log.debug("Saving author of the post to cache");
            UserForNewsFeedDto user = userServiceClient.getUserForNewsFeed(post.getAuthorId());

            UserCache userCache = userMapper.toUserCache(user);
            redisUserService.saveUserToCache(userCache);
        }
    }

    private PostEvent createPostEvent(Post post) {
        List<Long> followerIds = userServiceClient.getFollowers(post.getAuthorId());
        return PostEvent.builder()
                .postId(post.getId())
                .followerIds(followerIds)
                .build();
    }

    public PostForNewsFeedDto getPostForNewsFeed(long postId, long userId) {
        log.info("Trying to get post under id {} for news feed for user under id {}", postId, userId);
        Post post = postRepository.findByPostIdWithLikes(postId);
        validator.validatePostIsNotNull(post, postId);
        return postMapper.toPostForNewsFeedDto(post);
    }

    public List<PostForNewsFeedDto> getLastNPostsByUserIdStartingFromPost(long userId, int numPosts, long lastPostId) {
        log.info("Trying to get last {} posts for user under id {}. Last viewed post id: {}", userId, numPosts, lastPostId);
        Pageable pageable = PageRequest.of(0, numPosts);
        Optional<UserCache> userOptional = redisUserRepository.findById(userId);
        List<Long> followeeIds = userOptional.isEmpty() ? userServiceClient.getUserForNewsFeed(userId).followeeIds() : userOptional.get().getFolloweeIds();
        List<Post> posts = postRepository.findLastNPostsByUserIdStartingFromPostWithLikes(followeeIds, lastPostId, pageable);
        return postMapper.toPostForNewsFeedDto(posts);
    }
}
