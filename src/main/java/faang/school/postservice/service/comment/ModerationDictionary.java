package faang.school.postservice.service.comment;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ModerationDictionary {

    public static final String BAD_WORDS_LIST = "/bad_words_list.txt";

    @Value("${comment.word-split-regex}")
    private String wordSplitRegex;
    private Set<String> badWords;

    @PostConstruct
    public void loadBadWords() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                Objects.requireNonNull(getClass().getResourceAsStream(BAD_WORDS_LIST))))) {

            badWords = reader.lines()
                    .map(String::toLowerCase)
                    .collect(Collectors.toSet());

        } catch (IOException ex) {
            throw new RuntimeException("Failed to load forbidden words!", ex);
        }
    }

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
