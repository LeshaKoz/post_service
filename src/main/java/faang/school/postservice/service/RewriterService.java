package faang.school.postservice.service;

import faang.school.postservice.client.RewriterClient;
import faang.school.postservice.dto.rewriterai.RewriterAiResponse;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RewriterService {

    private final RewriterClient rewriterClient;

    @Value("${rewriter.token}")
    private String rewriterToken;

    @Retryable(
            retryFor = FeignException.class,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public String rewriteText(String text) {
        RewriterAiRequest request = new RewriterAiRequest(text);
        RewriterAiResponse response = rewriterClient.rewrite(request, rewriterToken);
        return response.paraphrase();
    }

    @Recover
    public String rewriteTextRecover(FeignException e, String text) {
        log.error("Error rewriting text {}", e.getMessage());
        return text;
    }

    private String sendPost(String text) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + rewriterToken);
        headers.set("Content-Type", "application/json");


    }

}
