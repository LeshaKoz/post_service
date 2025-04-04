package faang.school.postservice.service;

import com.google.common.collect.Lists;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private static final String POST = "Post";

    private final PostRepository postRepository;
    private final ExecutorService executorService;

    public Post getPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException(POST, postId));

        log.info("Get post with id {}", postId);
        return post;
    }

    @Async("fileUploadTaskExecutor")
    public void publishSchedulePosts() {
        List<Post> postsToPublish = postRepository.findReadyToPublish();

        if (postsToPublish.isEmpty()) {
            return;
        }

        List<List<Post>> batches = Lists.partition(postsToPublish, 1000);

        List<CompletableFuture<Void>> futures = batches.stream()
                .map(batch -> CompletableFuture.runAsync(() -> publishBatch(batch), executorService))
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    private void publishBatch(List<Post> batch) {
        try {
            batch.forEach(post -> {
                post.setPublished(true);
                post.setPublishedAt(LocalDateTime.now());
            });
            postRepository.saveAll(batch);
        } catch (Exception e) {
            log.error("Failed to publish batch of size {}", batch.size(), e);
            throw new IllegalArgumentException("Failed to publish batch of size");
        }

    }
}
