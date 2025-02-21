package faang.school.postservice.service;

import faang.school.postservice.model.SpellCheckResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@RequiredArgsConstructor
@Service
public class SpellCheckerService {
    private final RestTemplate restTemplate;

    @Value("${app.speller.url}")
    private String spellerUrl;

    @Retryable(maxAttemptsExpression = "${app.speller.retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${app.speller.retry.backoffDelay}"))
    public String correctTextWithYandexSpeller(String text) {
        try {
            String encodedText = URLEncoder.encode(text, StandardCharsets.UTF_8);

            URI uri = UriComponentsBuilder
                    .fromHttpUrl(spellerUrl)
                    .queryParam("text", encodedText)
                    .build()
                    .toUri();

            SpellCheckResponse[] responses = restTemplate.getForObject(uri, SpellCheckResponse[].class);

            return applyCorrection(text, responses);
        } catch (RestClientException e) {
            log.error("Error while correcting text using Yandex Speller: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private String applyCorrection(String text, SpellCheckResponse[] responses) {
        if (responses == null || responses.length == 0) {
            log.debug("No corrections found for text: {}", text);
            return text;
        }

        StringBuilder correctedText = new StringBuilder(text);
        int offset = 0;

        for (SpellCheckResponse response : responses) {
            if (response.getS() == null || response.getS().isEmpty()) {
                log.warn("No suggestions found for the word: {}", response.getWord());
                continue;
            }

            int start = response.getPos() + offset;
            int end = start + response.getLen();

            if (start < 0 || end > correctedText.length()) {
                log.warn("Invalid positions for replacement: start={}, end={}, textLength={}",
                        start, end, correctedText.length());
                continue;
            }

            String replacement = response.getS().get(0);
            correctedText.replace(start, end, replacement);

            offset += replacement.length() - response.getLen();
        }
        log.debug("Corrected text: {}", correctedText);
        return correctedText.toString();
    }
}
