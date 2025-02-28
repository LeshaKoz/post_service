package faang.school.postservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "api")
@Getter
@Setter
public class SwaggerConfig {
    private String title;
    private String version;
    private String description;

    @Bean
    public OpenAPI customOpenApi() {
        return new OpenAPI()
                .info( new Info()
                        .title(title)
                        .version(version)
                        .description(description));
    }
}
