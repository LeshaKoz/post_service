package faang.school.postservice.client;

import faang.school.postservice.dto.commentAnalyzer.request.CommentRequestDto;
import faang.school.postservice.dto.commentAnalyzer.response.ToxicityScoreDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class CommentAnalyzerClient {
    private final WebClient commentAnalyzerWebClient;
    @Value("${services.comment-analyzer.api-key}")
    String apiKey;
    public ToxicityScoreDto analyzeComment(String text) {
        System.out.println(new CommentRequestDto(text));
        return commentAnalyzerWebClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/comments:analyze")
                        .queryParam("key", apiKey)
                        .build())
                .header("Content-Type", "application/json")
                .bodyValue(new CommentRequestDto(text))
                .retrieve()
                .bodyToMono(ToxicityScoreDto.class)
                .block();
    }
}
