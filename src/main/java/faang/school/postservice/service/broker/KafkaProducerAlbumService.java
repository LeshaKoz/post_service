package faang.school.postservice.service.broker;

import faang.school.postservice.dto.album.AlbumCreateEvent;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerAlbumService {
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${spring.kafka.album-create-topic-name}")
    private String albumCreateTopicName;

    public void sendAlbumCreate(AlbumCreateEvent event) {
        JSONObject jsonObject = new JSONObject(event);
        String json = jsonObject.toString();
        kafkaTemplate.send(albumCreateTopicName, json);
    }
}
