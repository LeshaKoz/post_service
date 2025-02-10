package faang.school.postservice.service;

import faang.school.postservice.dto.rewriter.RewriterRequest;
import faang.school.postservice.dto.rewriter.RewriterResponse;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class RewriterService {

    private final WebClient webClient;
    @Value("${rewriter.token}")
    private String rewriterToken;

    @Value("${rewriter.api}")
    private String rewriterApi;

    @Retryable(
            retryFor = FeignException.class,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public String rewriteText(String text) {
        RewriterRequest request = new RewriterRequest(text);
        RewriterResponse response = webClient
                .post()
                .uri(rewriterApi)
                .header("Authorization", "Bearer " + rewriterToken)
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .body(Mono.just(request), RewriterRequest.class)
                .retrieve()
                .bodyToMono(RewriterResponse.class)
                .block();

        return response != null ? response.paraphrase() : "";
    }

    @Recover
    public String rewriteTextRecover(FeignException e, String text) {
        log.error("Error rewriting text {}", e.getMessage());
        return text;
    }
}
