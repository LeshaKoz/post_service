package faang.school.postservice.service.comment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


@Configuration
public class BadWord {

    @Bean
    public Set<String> badWords(@Value("${comment.path-to-dictionary}") String pathToFile) {
        Set<String> badWords;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                Objects.requireNonNull(getClass().getResourceAsStream(pathToFile))))) {

            badWords = reader.lines()
                    .map(String::toLowerCase)
                    .collect(Collectors.toSet());

        } catch (IOException ex) {
            throw new RuntimeException("Failed to load forbidden words!", ex);
        }

        return badWords;
    }
}
