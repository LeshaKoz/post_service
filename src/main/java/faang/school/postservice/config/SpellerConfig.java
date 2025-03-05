package faang.school.postservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.speller")
@Getter
@Setter
public class SpellerConfig {
    private int maxRequestUriLength;
    private int maxContentLength;
    private int baseUrlLength;
    private int separatorLength;
}