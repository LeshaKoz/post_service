package faang.school.postservice.config.kafka;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import java.util.Map;

@Setter
@Getter
@ConfigurationProperties(prefix = "spring.data.kafka")
public class KafkaProperties {
    private Map<String, Topic> topics;
    private String bootstrapServers;

    @Setter
    @Getter
    public static class Topic {
        private String name;
        private int partitions;
        private short replicas;
    }
}