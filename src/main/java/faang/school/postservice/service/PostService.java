package faang.school.postservice.service;

import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private static final String POST = "Post";

    private final GrammarBotService grammarBotService;
    private final PostRepository postRepository;

    public Post getPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException(POST, postId));

        log.info("Get post with id {}", postId);
        return post;
    }

    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 2000, multiplier = 2))
    public void correctUnpublishedPosts() {
        List<Post> posts = postRepository.findReadyToPublish();

        for (Post post : posts) {
            try {
                String correctedText = grammarBotService.checkGrammar(post.getContent());
                post.setContent(correctedText);
                postRepository.save(post);
            } catch (Exception e) {
                log.error("Error checking post {}: {}", post.getId(), e.getMessage());
            }
        }
    }
}
