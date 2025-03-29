package faang.school.postservice.config;

import faang.school.postservice.exception.LanguageToolException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Configuration
public class LanguageToolClientConfig {
    @Value("${servers.language-tool.base-url}")
    private String baseUrl;

    @Bean
    public WebClient LanguageToolWebClient() {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .filter(errorHandlingFilter())
                .build();
    }

    private ExchangeFilterFunction errorHandlingFilter() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            if (clientResponse.statusCode().isError()) {
                return clientResponse
                        .bodyToMono(String.class)
                        .defaultIfEmpty("No error details")
                        .flatMap(errorBody -> Mono.error(
                                new LanguageToolException(
                                        "Error while accessing language tool client. " + errorBody
                                )
                        ));
            }
            return Mono.just(clientResponse);
        });
    }
}
