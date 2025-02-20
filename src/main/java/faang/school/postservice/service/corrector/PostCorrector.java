package faang.school.postservice.service.corrector;

import faang.school.postservice.client.spell.SpellServiceClient;
import faang.school.postservice.dto.spell.SpellDto;
import faang.school.postservice.exception.IntegrationException;
import faang.school.postservice.model.Post;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostCorrector {
    private final SpellServiceClient spellServiceClient;

    public void correctContentPost(Post post) {
        try {
            String originalText = post.getContent();
            List<SpellDto> spellList = spellServiceClient.checkText(originalText);

            if (spellList.isEmpty()) {
                return;
            }

            post.setContent(getCorrectText(originalText, spellList));
        } catch (FeignException ex) {
            String errorMessage = "Вызов сервиса для корректировки орфографии завершился с ошибкой";
            log.error(errorMessage, ex);
            throw new IntegrationException(errorMessage);
        }
    }

    private String getCorrectText(String originalText, List<SpellDto> spells) {
        List<String> suggestions;
        String replacement;
        int startIndex;
        int endIndex;

        var correctedText = new StringBuilder(originalText);

        for (SpellDto spell : spells) {
            suggestions = spell.getSuggestions();

            if (suggestions.isEmpty()) {
                return originalText;
            }

            replacement = spell.getSuggestions().get(0);
            startIndex = spell.getReplacementStartIndex();
            endIndex = startIndex + spell.getCorrectWordLength();

            correctedText.replace(startIndex, endIndex, replacement);
        }
        return correctedText.toString();
    }
}
