package faang.school.postservice.service;

import faang.school.postservice.model.SpellCheckResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SpellCheckerServiceTest {

    private static final String SPELLER_URL = "https://speller.yandex.net/services/spellservice.json/checkText";
    private static final String TEST_TEXT = "helo";
    private static final String CORRECTED_TEXT = "hello";

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private SpellCheckerService spellCheckerService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(spellCheckerService, "spellerUrl", SPELLER_URL);
    }

    @Test
    public void testCorrectTextWithYandexSpeller_Success() {
        URI uri = UriComponentsBuilder
                .fromHttpUrl(SPELLER_URL)
                .queryParam("text", URLEncoder.encode(TEST_TEXT, StandardCharsets.UTF_8))
                .build()
                .toUri();

        SpellCheckResponse[] responses = new SpellCheckResponse[]{
                new SpellCheckResponse(TEST_TEXT, List.of(CORRECTED_TEXT), 0, 4)
        };

        when(restTemplate.getForObject(uri, SpellCheckResponse[].class)).thenReturn(responses);

        String correctedText = spellCheckerService.correctTextWithYandexSpeller(TEST_TEXT);

        assertEquals(CORRECTED_TEXT, correctedText);
    }

    @Test
    public void testCorrectTextWithYandexSpeller_NoCorrections() {
        URI uri = UriComponentsBuilder
                .fromHttpUrl(SPELLER_URL)
                .queryParam("text", URLEncoder.encode(CORRECTED_TEXT, StandardCharsets.UTF_8))
                .build()
                .toUri();

        when(restTemplate.getForObject(uri, SpellCheckResponse[].class)).thenReturn(new SpellCheckResponse[0]);

        String correctedText = spellCheckerService.correctTextWithYandexSpeller(CORRECTED_TEXT);

        assertEquals(CORRECTED_TEXT, correctedText);
    }

    @Test
    public void testCorrectTextWithYandexSpeller_ApiError() {
        URI uri = UriComponentsBuilder
                .fromHttpUrl(SPELLER_URL)
                .queryParam("text", URLEncoder.encode(TEST_TEXT, StandardCharsets.UTF_8))
                .build()
                .toUri();

        when(restTemplate.getForObject(uri, SpellCheckResponse[].class)).thenThrow(new RuntimeException("API error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            spellCheckerService.correctTextWithYandexSpeller(TEST_TEXT);
        });

        assertEquals("API error", exception.getMessage());
    }
}