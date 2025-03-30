package faang.school.postservice.config.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Data
@Component
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "spring.kafka")
public class KafkaProperties {
    private String bootstrapServers;
    private int poolSize;
    private Map<String, NewTopic> topics = new HashMap<>();
    private ConsumerProperties consumer;
    private ProducerProperties producer;

    @Getter
    @Setter
    public static class ConsumerProperties {
        private String groupId;
        private String autoOffsetReset;
        private String deserializerTrustedPackages;
    }

    @Getter
    @Setter
    public static class ProducerProperties {
        private int acks;
        private int retries;
    }
}
