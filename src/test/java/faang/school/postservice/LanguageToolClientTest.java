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
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LanguageToolClientTest {
    @InjectMocks
    private LanguageToolClient languageToolClient;

    @Mock
    private RestTemplate restTemplate;

    private String text;
    private final String language = "auto";
    private final String baseUrl = "https://api.languagetool.org/v2";

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(languageToolClient, "baseUrl", "https://api.languagetool.org/v2");
    }

    @Test
    public void testGetCorrectedText_returnsOriginalText() {
        text = "This is text";
        LanguageToolResponseDto response = new LanguageToolResponseDto();
        response.setMatches(Collections.emptyList());

        when(restTemplate.exchange(eq(baseUrl + "/check"), any(HttpMethod.class), any(),
                eq(LanguageToolResponseDto.class))).thenReturn(ResponseEntity.ok(response));

        String result = languageToolClient.getCorrectedText(text, language);

        assertEquals(text, result);
        verify(restTemplate, times(1))
                .exchange(eq(baseUrl + "/check"), any(), any(), eq(LanguageToolResponseDto.class));
    }

    @Test
    public void testGetCorrectedText_returnsCorrectedText_singleMatch() {
        text = "Thhis is text";
        LanguageToolResponseDto response = new LanguageToolResponseDto();
        response.setMatches(new ArrayList<>(List.of(
                new GrammarMatch(List.of(new ReplacementValueDto("This")), 0, 5)))
        );

        when(restTemplate.exchange(eq(baseUrl + "/check"), any(HttpMethod.class), any(),
                eq(LanguageToolResponseDto.class))).thenReturn(ResponseEntity.ok(response));

        String result = languageToolClient.getCorrectedText(text, language);

        assertEquals("This is text", result);
        verify(restTemplate, times(1))
                .exchange(eq(baseUrl + "/check"), any(), any(), eq(LanguageToolResponseDto.class));
    }

    @Test
    public void testGetCorrectedText_returnsCorrectedText_multipleMatches() {
        text = "Thhis are text";
        LanguageToolResponseDto response = new LanguageToolResponseDto();
        response.setMatches(new ArrayList<>(Arrays.asList(
                new GrammarMatch(List.of(new ReplacementValueDto("This")), 0, 5),
                new GrammarMatch(List.of(new ReplacementValueDto("is")), 6, 3)))
        );

        when(restTemplate.exchange(eq(baseUrl + "/check"), any(HttpMethod.class), any(),
                eq(LanguageToolResponseDto.class))).thenReturn(ResponseEntity.ok(response));

        String result = languageToolClient.getCorrectedText(text, language);

        assertEquals("This is text", result);
        verify(restTemplate, times(1))
                .exchange(eq(baseUrl + "/check"), any(), any(), eq(LanguageToolResponseDto.class));
    }
}
