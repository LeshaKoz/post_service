package faang.school.postservice.client;

import faang.school.postservice.dto.commentAnalyzer.request.CommentRequestDto;
import faang.school.postservice.dto.commentAnalyzer.response.ToxicityScoreDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class CommentAnalyzer {
    private final WebClient commentAnalyzerClient;

    @Value("${services.comment-analyzer.api-key}")
    String apiKey;

    public ToxicityScoreDto analyzeComment(String text) {
        return commentAnalyzerClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/comments:analyze")
                        .queryParam("key", apiKey)
                        .build())
                .bodyValue(new CommentRequestDto(text))
                .retrieve()
                .bodyToMono(ToxicityScoreDto.class)
                .block();
    }
}
