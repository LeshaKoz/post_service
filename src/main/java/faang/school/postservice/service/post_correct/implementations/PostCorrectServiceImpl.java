package faang.school.postservice.service.post_correct.implementations;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.post.PostServiceConstants;
import faang.school.postservice.exception.AIIntegrationException;
import faang.school.postservice.exception.JsonNotReadException;
import faang.school.postservice.exception.PostNotCorrectedException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.post_correct.interfaces.PostCorrectService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Getter
@Slf4j
public class PostCorrectServiceImpl implements PostCorrectService {
    private final PostRepository postRepository;
    private final TransactionTemplate transactionTemplate;
    private final ObjectMapper objectMapper;
    private final WebClient webClient;

    @Override
    public CompletableFuture<Void> correctPost(Post post, ExecutorService executor) {
        return checkSpellingWithRetry(post.getContent())
                .orTimeout(PostServiceConstants.TimeOut.CHECK_SPELLING_TIMEOUT, TimeUnit.SECONDS)
                .thenCompose(correctedContent -> {
                    log.info("Received corrected content: {}", correctedContent);
                    return CompletableFuture.supplyAsync(() ->
                            transactionTemplate.execute(status -> {
                                post.setContent(correctedContent);
                                post.setUpdatedAt(LocalDateTime.now());
                                Post savedPost = postRepository.save(post);
                                log.info("Successfully corrected post with id {}", savedPost.getId());
                                return savedPost;
                            }), executor);
                })
                .thenAccept(savedPost -> {
                })
                .exceptionally(throwable -> {
                    log.error("Failed to correct post with id {} after retries", post.getId(), throwable);
                    throw new PostNotCorrectedException(post.getId(),
                            "failed to correct post with %d after retries".formatted(post.getId()));
                });
    }

    @Override
    public CompletableFuture<String> checkSpellingWithRetry(String content) {
        String jsonRequest = String.format(
                "{\"cmd\":\"autocorrect\",\"lang\":\"en_US\",\"text\":\"%s\"}", content.replace("\"", "\\\"")
        );
        Mono<String> responseMono = webClient.post()
                .bodyValue(jsonRequest)
                .retrieve()
                .onStatus(status -> !status.is2xxSuccessful(),
                        clientResponse -> Mono.error(() ->
                                new AIIntegrationException("Unexpected code " + clientResponse.statusCode() +
                                        " received from the proofreader")))
                .bodyToMono(String.class)
                .map(responseBody -> parseCorrectedContent(responseBody, content))
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                        .maxBackoff(Duration.ofSeconds(8))
                        .filter(throwable -> throwable instanceof IOException || throwable instanceof RuntimeException));
        return responseMono.toFuture();
    }

    @Override
    public String parseCorrectedContent(String responseBody, String originalContent) {
        try {
            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode correctedNode = rootNode.path("corrected");
            if (correctedNode.isMissingNode() || correctedNode.isNull()) {
                return originalContent;
            }
            return correctedNode.asText();
        } catch (IOException e) {
            throw new JsonNotReadException(
                    "IO Exception occurred while parsing response to check spelling: " + e.getMessage());
        } catch (Exception e) {
            throw new JsonNotReadException("Unexpected error in parsing to check spelling: " + e.getMessage());
        }
    }
}
