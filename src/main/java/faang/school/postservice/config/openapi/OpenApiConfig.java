package faang.school.postservice.config.openapi;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Сервис работы с постами")
                        .description("API для управления пользователями")
                        .version("1.0")
                        .contact(new Contact()
                                .name("Команда Kelpie team (Stream 8)")
                                .url("https://kelpie-stream8.com"))
                        .license(new License()
                                .name("FAANG School")
                                .url("https://www.faang.school/"))
                );
    }
}