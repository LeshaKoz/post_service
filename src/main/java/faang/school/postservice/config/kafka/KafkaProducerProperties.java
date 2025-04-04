package faang.school.postservice.config.kafka;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@ConfigurationProperties(prefix = "app.kafka.topic.producer")
public class KafkaProducerProperties {

    /** Топик для отправки сообщения о создании поста */
    private Topic posts;

    /** Структура для хранения настроек конкретного топика */
    @Data
    public static class Topic {

        @NotBlank
        private String name;

        @NotNull
        private Boolean enabled;
    }
}
