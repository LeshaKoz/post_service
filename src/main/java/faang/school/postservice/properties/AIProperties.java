package faang.school.postservice.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("ai")
public class AIProperties {
    private String apiKey;
    private String grammarPrompt;
    private String chatUri;
    private String baseUrl;
    private String model;
}
