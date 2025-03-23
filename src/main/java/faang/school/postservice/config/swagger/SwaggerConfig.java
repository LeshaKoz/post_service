package faang.school.postservice.config.swagger;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Post Service API")
                        .version("1.0")
                        .description("API для управления постами в сервисе Post Service"))
                .addServersItem(new Server()
                        .url("http://localhost:8081")
                        .description("Local Development Server"));
    }

}
