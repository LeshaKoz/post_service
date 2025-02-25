package faang.school.postservice.service.moderate;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

@Component
public class ModerationDictionary {

    private Pattern profanityPattern;

    @Value("${moderation.bad-words-file}")
    private String badWordsFilePath;

    @PostConstruct
    private void init() throws IOException, URISyntaxException {
        String regex = Files.readString(Paths.get(Objects.requireNonNull(getClass().getClassLoader().getResource(badWordsFilePath)).toURI()));
        profanityPattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    }

    public boolean containsBadWords(String text) {
        Matcher matcher = profanityPattern.matcher(text);
        return matcher.find();
    }
}

