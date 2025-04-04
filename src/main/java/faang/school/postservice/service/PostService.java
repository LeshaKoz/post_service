package faang.school.postservice.service;

import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.moderation.ModerationDictionary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private static final String POST = "Post";

    private final PostRepository postRepository;
    private final ModerationDictionary moderationDictionary;

    @Value("${moderation.batch-size}")
    private int batchSize;

    @Transactional
    public List<Post> getUnverifiedPosts() {
        return postRepository.findByVerifiedAtIsNull();
    }

    @Transactional
    public void moderatePosts(List<Post> posts) {
        posts.forEach(post -> {
            boolean hasBadWords = moderationDictionary.containsBadWord(post.getContent());
            post.setVerified(!hasBadWords);
            post.setVerifiedAt(LocalDateTime.now());
            logModerationResult(post, hasBadWords);
        });
        postRepository.saveAll(posts);
    }

    private void logModerationResult(Post post, boolean hasBadWords) {
        if (hasBadWords) {
            log.warn("Post moderation FAILED. Post ID: {}, Author ID: {}. Contains bad words. Marked as unverified.",
                    post.getId(), post.getAuthorId());
        } else {
            log.info("Post moderation PASSED. Post ID: {}, Author ID: {}. Marked as verified.",
                    post.getId(), post.getAuthorId());
        }
    }

    public void moderateAllUnverifiedPosts() {
        List<Post> unverifiedPosts = getUnverifiedPosts();

        List<List<Post>> batches = partitionList(unverifiedPosts, batchSize);

        batches.parallelStream().forEach(this::moderatePosts);
    }

    private <T> List<List<T>> partitionList(List<T> list, int batchSize) {
        return IntStream.range(0, (list.size() + batchSize - 1) / batchSize)
                .mapToObj(i -> list.subList(i * batchSize, Math.min((i + 1) * batchSize, list.size())))
                .collect(Collectors.toList());
    }


    public Post getPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException(POST, postId));

        log.info("Get post with id {}", postId);
        return post;
    }
}
