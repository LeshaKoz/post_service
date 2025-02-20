package faang.school.postservice.config.comment;

import faang.school.postservice.config.YamlPropertySourceFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@PropertySource(value = "classpath:moderation-dictionary.yaml", factory = YamlPropertySourceFactory.class)
public class ModerationDictionaryConfig {

    @Value("${moderation.dictionary}")
    private List<String> dictionary;

    @Bean(name = "badWords")
    public List<String> badWords() {
        return dictionary;
    }

}

