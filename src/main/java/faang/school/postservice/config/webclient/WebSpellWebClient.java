package faang.school.postservice.config.webclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebSpellWebClient {
    @Value("${webspell.api.url}")
    private String webSpellApiUrl;

    @Value("${webspell.api.content-type}")
    private String webSpellApiContentType;

    @Value("${webspell.api.key}")
    private String webSpellApiKey;

    @Value("${webspell.api.host}")
    private String webSpellApiHost;

    @Bean
    public WebClient webSpellWebClient() {
        return WebClient.builder()
                .baseUrl(webSpellApiUrl)
                .defaultHeader("Content-Type", webSpellApiContentType)
                .defaultHeader("x-rapidapi-key", webSpellApiKey)
                .defaultHeader("x-rapidapi-host", webSpellApiHost)
                .build();
    }
}
