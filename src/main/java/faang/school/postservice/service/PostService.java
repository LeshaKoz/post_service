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
import java.util.ArrayList;
import java.util.List;

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
    public void moderateAllUnverifiedPosts() {
        List<Post> unverifiedPosts = postRepository.findByVerifiedAtIsNull();
        List<List<Post>> batches = partitionList(unverifiedPosts, batchSize);
        batches.forEach(batch -> {
            batch.forEach(post -> {
                boolean hasBadWords = moderationDictionary.containsBadWord(post.getContent());
                post.setVerified(!hasBadWords);
                post.setVerifiedAt(LocalDateTime.now());
                logModerationResult(post, hasBadWords);
            });
            postRepository.saveAll(batch);
        });
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

    private <T> List<List<T>> partitionList(List<T> list, int batchSize) {
        List<List<T>> partitions = new ArrayList<>();
        for (int i = 0; i < list.size(); i += batchSize) {
            int end = Math.min(i + batchSize, list.size());
            partitions.add(list.subList(i, end));
        }
        return partitions;
    }

    public Post getPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException(POST, postId));

        log.info("Get post with id {}", postId);
        return post;
    }
}
