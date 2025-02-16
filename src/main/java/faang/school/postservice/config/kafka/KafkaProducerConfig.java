package faang.school.postservice.config.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;

@Configuration
@EnableKafka
public class KafkaProducerConfig {

    private final KafkaProperties kafkaProperties;

    @Value("${spring.kafka.topics.authorization-topic}")
    private String authorizationTopicName;

    @Value("${spring.kafka.topics.post-view-topic}")
    private String postViewTopicName;

    public KafkaProducerConfig(KafkaProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
    }

    @Bean
    public <V> ProducerFactory<String, V> producerFactory() {
        return new org.springframework.kafka.core.DefaultKafkaProducerFactory<>(kafkaProperties.buildProducerProperties());
    }

    @Bean
    public <V> KafkaTemplate<String, V> kafkaTemplate(ProducerFactory<String, V> producerFactory, ObjectMapper objectMapper) {
        KafkaTemplate<String, V> template = new KafkaTemplate<>(producerFactory);
        template.setMessageConverter(new StringJsonMessageConverter(objectMapper));
        return template;
    }

    @Bean
    public NewTopic authorizationTopic() {
        return new NewTopic(authorizationTopicName, 1, (short) 1);
    }

    @Bean
    public NewTopic postViewTopic() {
        return new NewTopic(postViewTopicName, 1, (short) 1);
    }
}
