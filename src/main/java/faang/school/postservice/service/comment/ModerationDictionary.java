package faang.school.postservice.service.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class ModerationDictionary {

    @Value("${comment.word-split-regex}")
    private  String wordSplitRegex;
    private final Set<String> badWords;

    public boolean containsForbiddenWords(String text) {
        if (text == null || text.isBlank()) {
            return false;
        }

        String lowerText = text.toLowerCase();
        return badWords.stream().anyMatch(word ->
                lowerText.contains(word)
                        || Arrays.asList(lowerText.split(wordSplitRegex)).contains(word)
        );
    }
}
