package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PostCreateRequestDto(
        @NotBlank
        String content,
        @NotNull
        Long authorId,
        @NotNull
        Long projectId,
        LocalDateTime scheduledAt) {
}
