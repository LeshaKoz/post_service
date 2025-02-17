package faang.school.postservice.service;

import faang.school.postservice.dto.grammar.GrammarReadDto;
import faang.school.postservice.exception.ExternalServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GrammarServiceTest {
    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private GrammarService grammarService;

    private GrammarReadDto[] mockResponse;


    @BeforeEach
    void setUp() {
        GrammarReadDto dto = new GrammarReadDto();
        dto.setWord("teh");
        dto.setHints(List.of("the"));
        mockResponse = new GrammarReadDto[]{dto};
    }

    @Test
    void correctText_ShouldReturnCorrectedText() {
        when(restTemplate.getForEntity(anyString(), eq(GrammarReadDto[].class)))
                .thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));

        String correctedText = grammarService.correctText("teh quick brown fox");
        assertEquals("the quick brown fox", correctedText);
    }

    @Test
    void correctText_ShouldThrowExternalServiceException_WhenServiceReturnsError() {
        when(restTemplate.getForEntity(anyString(), eq(GrammarReadDto[].class)))
                .thenReturn(new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR));

        assertThrows(
                ExternalServiceException.class,
                () -> grammarService.correctText("teh quick brown fox")
        );
    }
}
