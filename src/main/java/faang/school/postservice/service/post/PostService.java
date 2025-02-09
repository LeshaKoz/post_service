package faang.school.postservice.service.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.exception.PostAlreadyPublishedException;
import faang.school.postservice.exception.PostWasDeletedException;
import faang.school.postservice.exception.ProjectNotFoundException;
import faang.school.postservice.exception.UserNotFoundException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@RequiredArgsConstructor
@EnableAsync
@Service
public class PostService {

    @Value("${post-service.publish.batch-size}")
    private int batchSize;

    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;
    private final AsyncPostPublishPerformer publishPerformer;

    @Transactional
    public void createPostByUserId(Long userId, Post post) {
        doesUserExist(userId);
        post.setAuthorId(userId);
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());

        postRepository.save(post);
    }

    @Transactional
    public void createPostByProjectId(Long projectId, Post post) {
        doesProjectExist(projectId);
        post.setProjectId(projectId);
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());

        postRepository.save(post);
    }

    @Transactional
    public void publishPost(Long postId) {
        Post post = getPost(postId);

        if (post.getPublishedAt() != null) {
            throw new PostAlreadyPublishedException("The post has already been published");
        }

        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        postRepository.save(post);
    }

    @Transactional
    public void updatePost(Long postId, Post post) {
        Post existingPost = getPost(postId);

        existingPost.setContent(post.getContent());
        existingPost.setUpdatedAt(LocalDateTime.now());
        existingPost.setProjectId(post.getProjectId());

        postRepository.save(existingPost);
    }

    @Transactional
    public void softDeletePost(Long postId) {
        Post existingPost = getPost(postId);

        existingPost.setDeleted(true);

        postRepository.save(existingPost);
    }

    public Post getPostById(Long postId) {
        Post post = getPost(postId);

        if (post.isDeleted()) {
            throw new PostWasDeletedException("The post was deleted");
        }

        return post;
    }

    public List<Post> getNotPublishedPostsByUser(Long userId) {
        doesUserExist(userId);
        return postRepository
                .findByAuthorId(userId).stream()
                .filter(post -> !post.isPublished())
                .collect(Collectors.toList());
    }

    public List<Post> getNotPublishedPostsByProject(Long projectId) {
        doesProjectExist(projectId);
        return postRepository
                .findByProjectId(projectId).stream()
                .filter(post -> !post.isPublished())
                .collect(Collectors.toList());

    }

    public List<Post> getPublishedPostsByUser(Long userId) {
        doesUserExist(userId);
        return postRepository
                .findByAuthorIdWithLikes(userId).stream()
                .filter(Post::isPublished)
                .collect(Collectors.toList());
    }

    public List<Post> getPublishedPostsByProject(Long projectId) {
        doesProjectExist(projectId);
        return postRepository
                .findByProjectIdWithLikes(projectId).stream()
                .filter(Post::isPublished)
                .collect(Collectors.toList());
    }

    @Transactional
    public void savePost(Post post) {
        postRepository.save(post);
    }

    private void doesUserExist(Long userId) {
        try {
            userServiceClient.getUser(userId);
        } catch (FeignException.NotFound e) {
            throw new UserNotFoundException("User with id " + userId + " not found");
        }
    }

    @Transactional
    public void publishScheduledPosts() {
        List<Post> readyToPublishPosts = postRepository.findReadyToPublish();

        if (readyToPublishPosts.isEmpty()) {
            log.info("No one post to publish");
            return;
        }

        List<List<Post>> batches = ListUtils.partition(readyToPublishPosts, batchSize);

        batches.forEach(publishPerformer::publishBatch);
    }

    private void doesProjectExist(Long projectId) {
        try {
            projectServiceClient.getProject(projectId);
        } catch (FeignException.NotFound e) {
            throw new ProjectNotFoundException("Project with id " + projectId + " not found");
        }
    }

    private Post getPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));
    }

//    private void publishBatch(List<Post> posts) {
//        posts.forEach(post -> {
//            post.setPublished(true);
//            post.setPublishedAt(LocalDateTime.now());
//        });
//        postRepository.saveAll(posts);
//        log.info("Scheduled task #Publish post# completed");
//    }
}