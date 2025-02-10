package faang.school.postservice.service;

import faang.school.postservice.dto.grammar.GrammarReadDto;
import faang.school.postservice.exception.ExternalServiceException;
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

    public String correctText(String text) {
        String uri = UriComponentsBuilder.fromHttpUrl(
                        "https://speller.yandex.net/services/spellservice.json/checkText"
                )
                .queryParam("text", text.replaceAll(" ", "+"))
                .toUriString();

        ResponseEntity<GrammarReadDto[]> response = restTemplate
                .getForEntity(uri, GrammarReadDto[].class);

        if (response.getStatusCode().isError()) {
            log.error("Ошибка при вызове yandex speller. Код: {}, тело ответа: {}",
                    response.getStatusCode(), response.getBody());
            throw new ExternalServiceException("Ошибка при вызове сервиса орфографии");
        }

        GrammarReadDto[] dto = response.getBody();

        for (var error : requireNonNull(dto)) {
            text = text.replaceFirst(error.getWord(), error.getHints().get(0));
        }
        return text;
    }
}
