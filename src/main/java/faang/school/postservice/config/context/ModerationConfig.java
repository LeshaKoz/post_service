package faang.school.postservice.config.context;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@ConfigurationProperties(prefix = "app")
public class ModerationConfig {

    private Set<String> rudeWords = new HashSet<>();

    public void setRudeWords(Set<String> rudeWords) {
        this.rudeWords = rudeWords.stream().map(String::toLowerCase).collect(Collectors.toSet());
    }
}
