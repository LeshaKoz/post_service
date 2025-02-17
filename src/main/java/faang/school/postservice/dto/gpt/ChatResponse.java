package faang.school.postservice.dto.gpt;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {
    private static final String ROLE_ASSISTANT = "assistant";
    @NotNull
    @Size(min = 1)
    private List<Choice> choices;

    @Data
    public static class Choice {
        @NotNull
        private Message message;
    }

    public String findAssistanceContent() {
        return choices.stream()
                .filter(choice -> ROLE_ASSISTANT.equals(choice.getMessage().getRole()))
                .map(choice -> choice.getMessage().getContent())
                .findFirst()
                .orElseThrow();
    }
}
