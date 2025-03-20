package faang.school.postservice.config.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap.server.address}")
    private String bootstrapAddress;
    @Value("${spring.kafka.topics.analytic-topics.add-like-topic-name}")
    private String addLikeEventTopicName;

    @Bean("addLikeProducerFactory")
    public ProducerFactory<String, Object> defaultProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapAddress);
        configProps.put(
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class.getName());
        configProps.put(
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                JsonSerializer.class.getName());

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean("addLikeKafkaTemplate")
    public KafkaTemplate<String, Object> addLikeKafkaTemplate() {
        return new KafkaTemplate<>(defaultProducerFactory());
    }

    @Bean(name = "postViewEventTemplate")
    public KafkaTemplate<String, Object> postViewKafkaTemplate() {
        return new KafkaTemplate<>(defaultProducerFactory());
    }

    @Bean
    public String addLikeEventTopicName() {
        return addLikeEventTopicName;
    }

}
