package faang.school.postservice.service;

import faang.school.postservice.dto.grammar.GrammarReadDto;
import faang.school.postservice.exception.ExternalServiceException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import static java.util.Objects.requireNonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class GrammarService {
    private final RestTemplate restTemplate;

    @CircuitBreaker(name = "default", fallbackMethod = "fallback")
    @Retry(name = "default", fallbackMethod = "fallback")
    public String correctText(String text) {
        GrammarReadDto[] dto = requestSpelling(text);
        for (var error : requireNonNull(dto)) {
            text = text.replaceFirst(error.getWord(), error.getHints().get(0));
        }
        return text;
    }

    private GrammarReadDto[] requestSpelling(String text) {
        String uri = UriComponentsBuilder.fromHttpUrl(
                        "https://speller.yandex.net/services/spellservice.json/checkText"
                )
                .queryParam("text", text)
                .toUriString();

        ResponseEntity<GrammarReadDto[]> response = restTemplate
                .getForEntity(uri, GrammarReadDto[].class);

        if (response.getStatusCode().isError()) {
            log.error("Ошибка при вызове yandex speller. Код: {}, тело ответа: {}",
                    response.getStatusCode(), response.getBody());
            throw new ExternalServiceException("Ошибка при вызове сервиса орфографии");
        }
        return response.getBody();
    }

    public String fallback(Throwable throwable) {
        throw new ExternalServiceException("Ошибка при вызове сервиса орфографии");
    }
}
