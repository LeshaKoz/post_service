package faang.school.postservice.config.kafka;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaTopicConfig {

    @Value(value = "${spring.data.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Value(value = "${spring.data.kafka.topic.post.name}")
    private String postTopic;

    @Value("${spring.data.kafka.topic.post-view.name}")
    private String postViewsTopic;

    @Value(value = "${spring.data.kafka.topic.post.partitions}")
    private int postTopicNumPartitions;

    @Value("${spring.data.kafka.topic.post-view.partitions}")
    private int postViewsTopicNumPartitions;

    @Value(value = "${spring.data.kafka.topic.post.replicas}")
    private short postReplicationFactor;

    @Value("${spring.data.kafka.topic.post-view.replicas}")
    private short postViewsReplicationFactor;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
    }

    @Bean(name = "postsTopic")
    public NewTopic postsTopic() {
        return new NewTopic(postTopic, postTopicNumPartitions, postReplicationFactor);
    }

    @Bean(name = "postViewsTopic")
    public NewTopic postViewsTopic() {
        return new NewTopic(postViewsTopic, postViewsTopicNumPartitions, postViewsReplicationFactor);
    }
}
