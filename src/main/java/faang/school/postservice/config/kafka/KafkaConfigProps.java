package faang.school.postservice.config.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.HashMap;

@Component
@AllArgsConstructor
@NoArgsConstructor
@Data
@ConfigurationProperties(prefix = "spring.data.kafka")
public class KafkaConfigProps {
    private String bootstrapServers;
    private Map<String, NewTopic> topics = new HashMap<>();
    private ConsumerProps consumer;
    private ProducerProps producer;

    @Getter
    @Setter
    public static class ConsumerProps {
        private String groupId;
        private String deserializerTrustedPackages;
    }

    @Getter
    @Setter
    public static class ProducerProps {
        private String acknowledgement;
    }


}
