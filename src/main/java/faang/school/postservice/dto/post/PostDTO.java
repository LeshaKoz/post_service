package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PostDTO(
        Long id,
        @NotBlank(message = "Post must be contain content") String content,
        Long authorId,
        Long projectId,
        boolean published,
        LocalDateTime publishedAt,
        boolean deleted,
        LocalDateTime createdAt) {

    public PostDTO() {
        this(null, "", null, null, false, null, false, null);
    }
}
