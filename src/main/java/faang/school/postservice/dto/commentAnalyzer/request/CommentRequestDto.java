package faang.school.postservice.dto.commentAnalyzer.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentRequestDto {
    private CommentDto comment;
    private Map<String, Object> requestedAttributes;
    private boolean doNotStore;

    public CommentRequestDto(String text) {
        this.comment = new CommentDto(text);
        requestedAttributes = new HashMap<>();
        this.requestedAttributes.put("TOXICITY", new HashMap<>());
        this.requestedAttributes.put("SEVERE_TOXICITY", new HashMap<>());
        this.requestedAttributes.put("IDENTITY_ATTACK", new HashMap<>());
        this.requestedAttributes.put("INSULT", new HashMap<>());
        this.requestedAttributes.put("PROFANITY", new HashMap<>());
        this.requestedAttributes.put("THREAT", new HashMap<>());
        this.doNotStore = true;
    }
}
