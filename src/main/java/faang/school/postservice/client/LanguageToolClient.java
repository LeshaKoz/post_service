package faang.school.postservice.client;

import faang.school.postservice.dto.languageTool.GrammarMatch;
import faang.school.postservice.dto.languageTool.LanguageToolResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Comparator;

@Slf4j
@Component
@RequiredArgsConstructor
public class LanguageToolClient {
    private final WebClient languageToolWebClient;

    public Mono<String> getCorrectedText(String text, String language) {
        return languageToolWebClient.post()
                .uri("/check")
                .body(BodyInserters.fromFormData("text", text)
                        .with("language", language))
                .retrieve()
                .bodyToMono(LanguageToolResponseDto.class)
                .map(response -> correctText(text, response));
    }

    private String correctText(String text, LanguageToolResponseDto response) {
        if (response.getMatches() == null || response.getMatches().isEmpty()) {
            return text;
        }
        response.getMatches().sort(Comparator.comparingInt(GrammarMatch::getOffset));
        StringBuilder correctedText = new StringBuilder(text);
        int offsetCorrection = 0;

        for (GrammarMatch match : response.getMatches()) {
            int offset = match.getOffset() + offsetCorrection;
            int length = match.getLength();
            String replacement = match.getReplacements().isEmpty() ? ""
                    : match.getReplacements().get(0).getValue();

            offsetCorrection += replacement.length() - length;
            correctedText.replace(offset, offset + length, replacement);
        }
        return correctedText.toString();
    }
}
