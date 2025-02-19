package faang.school.postservice.service.corrector;

import faang.school.postservice.client.spell.SpellServiceClient;
import faang.school.postservice.dto.spell.SpellDto;
import faang.school.postservice.exception.IntegrationException;
import faang.school.postservice.model.Post;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostCorrectorTest {

    @Mock
    private SpellServiceClient spellServiceClient;

    @InjectMocks
    private PostCorrector postCorrector;

    private static final String UNCORRECTED_TEXT = "я люблю каров";
    private static final String CORRECTED_TEXT = "я люблю коров";

    @Test
    public void testSuccessCorrectContentPost() {
        Post post = Post.builder().content(UNCORRECTED_TEXT).build();
        SpellDto dto = SpellDto.builder()
                .replacementStartIndex(8)
                .correctWordLength(UNCORRECTED_TEXT.length())
                .suggestions(List.of("коров", "кадров"))
                .build();

        when(spellServiceClient.checkText(UNCORRECTED_TEXT)).thenReturn(List.of(dto));

        postCorrector.correctContentPost(post);

        assertEquals(CORRECTED_TEXT, post.getContent());
    }

    @Test
    public void testCorrectContentPostThrowException_If_SpellService_ThrowFeignException() {
        Post post = Post.builder().content(UNCORRECTED_TEXT).build();

        when(spellServiceClient.checkText(UNCORRECTED_TEXT)).thenThrow(FeignException.class);

        assertThrows(IntegrationException.class, () -> postCorrector.correctContentPost(post));
    }

    @Test
    public void testCorrectContentPost_If_SpellService_Returns_Empty() {
        Post post = Post.builder().content(UNCORRECTED_TEXT).build();

        when(spellServiceClient.checkText(UNCORRECTED_TEXT)).thenReturn(List.of());

        postCorrector.correctContentPost(post);

        assertEquals(UNCORRECTED_TEXT, post.getContent());
    }

    @Test
    public void testCorrectContentPost_If_SpellService_Returns_EmptySuggestions() {
        Post post = Post.builder().content(UNCORRECTED_TEXT).build();
        SpellDto dto = SpellDto.builder()
                .replacementStartIndex(8)
                .correctWordLength(UNCORRECTED_TEXT.length())
                .suggestions(List.of())
                .build();

        when(spellServiceClient.checkText(UNCORRECTED_TEXT)).thenReturn(List.of(dto));

        postCorrector.correctContentPost(post);

        assertEquals(UNCORRECTED_TEXT, post.getContent());
    }
}