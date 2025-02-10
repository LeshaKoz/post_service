package faang.school.postservice.service;

import faang.school.postservice.dto.grammar.GrammarReadDto;
import faang.school.postservice.exception.ExternalServiceException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;


@Service
@RequiredArgsConstructor
public class GrammarService {
    private final RestTemplate restTemplate;

    @Value("${external-services.keys.grammar-service}")
    private String key;

    @PostConstruct
    private void init() {
        restTemplate.getInterceptors().add(
                (request, body, execution) -> {
                    request.getHeaders().add(
                            "Authorization", "Basic " + key
                    );
                    return execution.execute(request, body);
                }
        );
    }

    public String checkGrammar(String text) {
        String uri = UriComponentsBuilder.fromHttpUrl("https://api.textgears.com/correct")
                .queryParam("text", text)
                .queryParam("language", "en-GB")
                .toUriString();

        GrammarReadDto dto = restTemplate.getForObject(uri, GrammarReadDto.class);

        if (dto == null || !dto.isStatus()) {
            throw new ExternalServiceException(dto == null ?
                    "Grammar service returned null body" : dto.getDescription()
            );
        }

        return dto.getCorrected();
    }
}
