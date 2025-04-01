package faang.school.postservice;

import faang.school.postservice.client.LanguageToolClient;
import faang.school.postservice.dto.languageTool.GrammarMatch;
import faang.school.postservice.dto.languageTool.LanguageToolResponseDto;
import faang.school.postservice.dto.languageTool.ReplacementValueDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LanguageToolClientTest {
    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private LanguageToolClient languageToolClient;

    private String text;
    private final String language = "auto";

    @BeforeEach
    public void setUp() {
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/check")).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any())).thenAnswer(inv -> requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
    }

    @Test
    public void testGetCorrectedText_returnsOriginalText() {
        text = "This is text";
        LanguageToolResponseDto response = new LanguageToolResponseDto();
        response.setMatches(Collections.emptyList());

        when(responseSpec.bodyToMono(LanguageToolResponseDto.class))
                .thenReturn(Mono.just(response));

        String result = languageToolClient.getCorrectedText(text, language);

        assertEquals(text, result);
        verify(webClient, times(1)).post();
        verify(requestBodyUriSpec, times(1)).uri("/check");
        verify(requestBodySpec, times(1)).body(any());
        verify(requestBodySpec, times(1)).retrieve();
    }

    @Test
    public void testGetCorrectedText_returnsCorrectedText_singleMatch() {
        text = "Thhis is text";
        LanguageToolResponseDto response = new LanguageToolResponseDto();
        response.setMatches(new ArrayList<>(List.of(
                new GrammarMatch(List.of(new ReplacementValueDto("This")), 0, 5)))
        );

        when(responseSpec.bodyToMono(LanguageToolResponseDto.class))
                .thenReturn(Mono.just(response));

        String result = languageToolClient.getCorrectedText(text, language);

        assertEquals("This is text", result);
        verify(webClient, times(1)).post();
        verify(requestBodyUriSpec, times(1)).uri("/check");
        verify(requestBodySpec, times(1)).body(any());
        verify(requestBodySpec, times(1)).retrieve();
    }

    @Test
    public void testGetCorrectedText_returnsCorrectedText_multipleMatches() {
        text = "Thhis are text";
        LanguageToolResponseDto response = new LanguageToolResponseDto();
        response.setMatches(new ArrayList<>(Arrays.asList(
                new GrammarMatch(List.of(new ReplacementValueDto("This")), 0, 5),
                new GrammarMatch(List.of(new ReplacementValueDto("is")), 6, 3)))
        );

        when(responseSpec.bodyToMono(LanguageToolResponseDto.class))
                .thenReturn(Mono.just(response));

        String result = languageToolClient.getCorrectedText(text, language);

        assertEquals("This is text", result);
        verify(webClient, times(1)).post();
        verify(requestBodyUriSpec, times(1)).uri("/check");
        verify(requestBodySpec, times(1)).body(any());
        verify(requestBodySpec, times(1)).retrieve();
    }
}
