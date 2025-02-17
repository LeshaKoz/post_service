package faang.school.postservice.service.impl;

import faang.school.postservice.dto.gpt.ChatRequest;
import faang.school.postservice.dto.gpt.ChatResponse;
import faang.school.postservice.dto.gpt.Message;
import faang.school.postservice.model.Post;
import faang.school.postservice.properties.AIProperties;
import faang.school.postservice.service.AIService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class AIServiceImpl implements AIService {
    private final AIProperties aiProperties;
    private final WebClient aiClient;

    public AIServiceImpl(AIProperties aiProperties) {
        this.aiProperties = aiProperties;
        this.aiClient = WebClient.builder()
                .baseUrl(aiProperties.getBaseUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Override
    @Valid
    @Retryable(backoff = @Backoff(delay = 21000))
    public String checkGrammarPost(Post post) {
        log.debug("Start check grammar post id = {}, text = {}", post.getId(), post.getContent());
        checkAIProperties();

        return Objects.requireNonNull(aiClient.post()
                        .uri(aiProperties.getChatUri())
                        .headers(httpHeaders -> httpHeaders.setBearerAuth(aiProperties.getApiKey()))
                        .bodyValue(createChatRequest(post.getContent()))
                        .retrieve()
                        .bodyToMono(ChatResponse.class)
                        .onErrorMap(error -> {
                            log.error("Error during grammar check: {}", error.getMessage());
                            return new RuntimeException("API request failed", error);
                        })
                        .doOnSuccess(chatResponse -> log.debug("End check grammar post id = {}, text = {}", post.getId(), chatResponse.findAssistanceContent()))
                        .block())
                        .findAssistanceContent();
    }

    private ChatRequest createChatRequest(String text) {
        return ChatRequest.builder()
                .model(aiProperties.getModel())
                .store(false)
                .messages(List.of(Message.builder()
                                .role("system").content(aiProperties.getGrammarPrompt()).build(),
                        Message.builder()
                                .role("user").content(text).build()))
                .build();
    }

    private void checkAIProperties() {
        if (aiProperties.getApiKey() == null || aiProperties.getApiKey().isEmpty()) {
            String apiKeyIsEmpty = "Api key is empty";
            log.info(apiKeyIsEmpty);
            throw new IllegalArgumentException(apiKeyIsEmpty);
        }
        if (aiProperties.getGrammarPrompt() == null || aiProperties.getGrammarPrompt().isEmpty()) {
            String grammarPromptIsEmpty = "Grammar prompt is empty";
            log.info(grammarPromptIsEmpty);
            throw new IllegalArgumentException(grammarPromptIsEmpty);
        }
        if (aiProperties.getChatUri() == null || aiProperties.getChatUri().isEmpty()) {
            String chatUriIsEmpty = "Chat URI is empty";
            log.info(chatUriIsEmpty);
            throw new IllegalArgumentException(chatUriIsEmpty);
        }
    }
}
