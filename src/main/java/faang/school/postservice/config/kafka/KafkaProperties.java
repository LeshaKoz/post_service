package faang.school.postservice.config.kafka;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.data.kafka.properties")
public record KafkaProperties(
    String bootstrapAddress,
    String postTopic
) {

}