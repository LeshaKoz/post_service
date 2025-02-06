package faang.school.postservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI postServiceInfo() {
        return new OpenAPI()
                .info(new Info()
                        .title("Post Service")
                        .description("Проект на Spring Boot с использованием PostgreSQL, Redis, Liquibase и Gradle. " +
                                "Включает тестирование с testcontainers, кэширование, обработку ошибок и обмен сообщениями" +
                                " через Redis pub/sub. Трёхслойная архитектура: Controller, Service, Repository. ")
                        .version("v1")
                );
    }
}
