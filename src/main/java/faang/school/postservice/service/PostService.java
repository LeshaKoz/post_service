package faang.school.postservice.service;

import faang.school.postservice.config.RestTemplateConfig;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.SpellCheckResponse;
import faang.school.postservice.repository.PostRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.net.http.HttpConnectTimeoutException;
import java.security.InvalidParameterException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final ExternalService externalService;
    private final RestTemplateConfig restTemplateConfig;

    public Post createDraft(Post post) {
        if (post.getAuthorId() != null && !externalService.userExists(post.getAuthorId())) {
            throw new InvalidParameterException("Post author does not exist! id:" + post.getAuthorId());
        }
        if (post.getProjectId() != null && !externalService.projectExists(post.getProjectId())) {
            throw new InvalidParameterException("Post project does not exist! id:" + post.getProjectId());
        }
        return postRepository.save(post);
    }

    public Post publish(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new DataValidationException("Specified post not found. Id:" + postId));
        if (post.isPublished()) {
            throw new DataValidationException("Post is already published. Id:" + postId);
        }
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        return postRepository.save(post);
    }

    public Post update(Post post) {
        Post originalPost = postRepository.findById(post.getId())
                .orElseThrow(() -> new DataValidationException("You are trying to update not existing post. Id:"
                        + post.getId()));
        if (!Objects.equals(originalPost.getAuthorId(), post.getAuthorId())
                || !Objects.equals(originalPost.getProjectId(), post.getProjectId())) {
            throw new DataValidationException("Post author cannot be changed!");
        }
        return postRepository.save(post);
    }

    public void delete(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new DataValidationException("Specified post not found. Id:" + postId));
        post.setDeleted(true);
        postRepository.save(post);
    }

    public Post get(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new DataValidationException("Specified post not found. Id:" + postId));
    }

    public List<Post> getDraftsByAuthorId(Long userId) {
        return postRepository.findByAuthorId(userId).stream()
                .filter(post -> !post.isDeleted() && !post.isPublished())
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .toList();
    }

    public List<Post> getDraftsByProjectId(Long projectId) {
        return postRepository.findByProjectId(projectId).stream()
                .filter(post -> !post.isDeleted() && !post.isPublished())
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .toList();
    }

    public List<Post> getPostsByAuthorId(Long userId) {
        return postRepository.findByAuthorId(userId).stream()
                .filter(post -> !post.isDeleted() && post.isPublished())
                .sorted(Comparator.comparing(Post::getPublishedAt).reversed())
                .toList();
    }

    public List<Post> getPostsByProjectId(Long projectId) {
        return postRepository.findByProjectId(projectId).stream()
                .filter(post -> !post.isDeleted() && post.isPublished())
                .sorted(Comparator.comparing(Post::getPublishedAt).reversed())
                .toList();
    }

    public void correctPosts() {
        List<Post> unpublishedPosts = postRepository.findByPublishedFalse();
        for (Post post : unpublishedPosts) {
            String correctedText = correctTextWithYandexSpeller(post.getContent());
            post.setContent(correctedText);
            postRepository.save(post);
        }
    }

    @Retryable(maxAttempts = 5, backoff = @Backoff(delay = 1000))
    private String correctTextWithYandexSpeller(String text) {
        String url = "https://speller.yandex.net/services/spellservice.json/checkText?text=" + text;
        SpellCheckResponse[] responses = restTemplateConfig.restTemplate().getForObject(url, SpellCheckResponse[].class);
        return applyCorrection(text, responses);
    }

    private String applyCorrection(String text, SpellCheckResponse[] responses) {
        return null;
    }
}
