package faang.school.postservice.client;

import faang.school.postservice.dto.languageTool.GrammarMatch;
import faang.school.postservice.dto.languageTool.LanguageToolResponseDto;
import faang.school.postservice.exception.LanguageToolException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Comparator;

@Slf4j
@Component
@RequiredArgsConstructor
public class LanguageToolClient {
    @Value("${servers.language-tool.base-url}")
    private String baseUrl;

    private final RestTemplate restTemplate;

    public String getCorrectedText(String text, String language) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("text", text);
        formData.add("language", language);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(formData, headers);

        LanguageToolResponseDto response = restTemplate.exchange(baseUrl + "/check", HttpMethod.POST,
                httpEntity, LanguageToolResponseDto.class).getBody();

        return response != null ? correctText(text, response) : text;
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

    private static class LanguageToolErrorHandler extends DefaultResponseErrorHandler {
        @Override
        public void handleError(ClientHttpResponse response) throws IOException {
            if (response.getStatusCode().isError()) {
                String errorBody = response.getBody() != null ? new String(response.getBody().readAllBytes()) :
                        "No error details";
                throw new LanguageToolException(
                        "Error while accessing language tool client. " + errorBody,
                        response.getStatusCode()
                );
            }
            super.handleError(response);
        }
    }
}
