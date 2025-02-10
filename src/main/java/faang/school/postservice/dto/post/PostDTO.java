package faang.school.postservice.dto.post;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@JsonSerialize
public record PostDTO(
        @JsonProperty("id") Long id,
        @NotBlank(message = "Post must be contain content") @JsonProperty("content") String content,
        @JsonProperty("authorId") Long authorId,
        @JsonProperty("projectId") Long projectId,
        @JsonProperty("published") boolean published,
        @JsonProperty("publishedAt") LocalDateTime publishedAt,
        @JsonProperty("deleted") boolean deleted,
        @JsonProperty("createdAt") LocalDateTime createdAt,
        @JsonProperty("updatedAt") LocalDateTime updatedAt) {

    public PostDTO() {
        this(null, "", null, null, false,
                null, false, null, null);
    }
}
