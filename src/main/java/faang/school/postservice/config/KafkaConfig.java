package faang.school.postservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {
    @Value("${spring.kafka.topics.like.name}")
    private String likeTopicName;

    @Value("${spring.kafka.topics.like.partitions}")
    private int partitions;

    @Value("${spring.kafka.topics.like.replicas}")
    private int replicas;

    @Bean(name = "likeTopic")
    public NewTopic likeTopic() {
        return TopicBuilder.name(likeTopicName)
                .partitions(partitions)
                .replicas(replicas)
                .build();
    }

    @Bean
    public KafkaTemplate<String, PostEvent> postEventKafkaTemplate(KafkaProperties kafkaProperties) {
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(kafkaProperties.buildProducerProperties()));
    }

    @Bean
    public KafkaTemplate<String, Long> authorBunKafkaTemplate(KafkaProperties kafkaProperties) {
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(kafkaProperties.buildProducerProperties()));
    }

    @Bean
    public KafkaTemplate<String, LikeEvent> likeEventKafkaTemplate(KafkaProperties kafkaProperties) {
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(kafkaProperties.buildProducerProperties()));
    }
}
