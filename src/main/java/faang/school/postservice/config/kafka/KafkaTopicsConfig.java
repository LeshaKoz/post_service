package faang.school.postservice.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicsConfig {

    @Value("${spring.kafka.topic.default-replicas}")
    private int defaultReplicas;

    @Value("${spring.kafka.topic.post-topic.name}")
    private String postsTopicName;

    @Value("${spring.kafka.topic.post-topic.partitions}")
    private int postsTopicPartitions;

    @Value("${spring.kafka.topic.comment-topic.name}")
    private String commentTopicName;

    @Value("${spring.kafka.topic.comment-topic.partitions}")
    private int commentTopicPartitions;

    @Value("${spring.kafka.topic.like-topic.name}")
    private String likeTopicName;

    @Value("${spring.kafka.topic.like-topic.partitions}")
    private int likeTopicPartitions;

    @Value("${spring.kafka.topic.view-topic.name}")
    private String viewTopicName;

    @Value("${spring.kafka.topic.view-topic.partitions}")
    private int viewTopicPartitions;

    @Bean
    public NewTopic postsTopic() {
        return TopicBuilder.name(postsTopicName)
                .partitions(postsTopicPartitions)
                .replicas(defaultReplicas)
                .build();
    }

    @Bean
    public NewTopic commentTopic() {
        return TopicBuilder.name(commentTopicName)
                .partitions(commentTopicPartitions)
                .replicas(defaultReplicas)
                .build();
    }

    @Bean
    public NewTopic likeTopic() {
        return TopicBuilder.name(likeTopicName)
                .partitions(likeTopicPartitions)
                .replicas(defaultReplicas)
                .build();
    }

    @Bean
    public NewTopic viewTopic() {
        return TopicBuilder.name(viewTopicName)
                .partitions(viewTopicPartitions)
                .replicas(defaultReplicas)
                .build();
    }
}