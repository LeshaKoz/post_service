package faang.school.postservice.config;

import faang.school.postservice.util.BaseContextTest;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.List;
import java.util.Map;

@ConditionalOnProperty(name = "enable.kafka", havingValue = "true")
@Configuration
public class KafkaTopicConfig {

    @Bean
    public KafkaAdmin kafkaAdmin() {
        return new KafkaAdmin(Map.of(
                "bootstrap.servers", BaseContextTest.KAFKA_CONTAINER.getBootstrapServers()
        ));
    }

    @Bean
    public List<NewTopic> topics() {
        return List.of(
                new NewTopic("user-create-post", 1, (short) 1)
        );
    }
}

