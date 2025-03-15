package faang.school.postservice.service.moderate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.app.PostServiceConfiguration;
import faang.school.postservice.service.moderate.model.WrongWordResourceItem;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ModerationDictionary {
    private final PostServiceConfiguration postServiceConfiguration;

    @Value("${moderation.bad-words-file-path}")
    private String badWordsFilePath;

    private List<WrongWordResourceItem> wrongWordResourceItems;

    @PostConstruct
    private void init() {
        Resource resource = new ClassPathResource(badWordsFilePath);
        ObjectMapper objectMapper = postServiceConfiguration.objectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try {
            wrongWordResourceItems = objectMapper.readValue(resource.getInputStream(), new TypeReference<>(){});
        } catch (IOException e) {
            log.error("Ошибка при загрузке словаря!", e);
        }
    }

    public boolean containsBadWords(String postContent) {
        postContent = postContent.replaceAll("@\"[^\\p{L}\\s]\"", "");
        List<String> splittedContent = Arrays.stream(postContent.split(" ")).toList();

        return wrongWordResourceItems.stream()
                .map(WrongWordResourceItem::getWord)
                .map(String::toLowerCase)
                .anyMatch(splittedContent::contains);
    }
}