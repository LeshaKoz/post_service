package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.Value;

import java.time.LocalDateTime;

@Value
public class RequestPostDto {
    Long id;
    @NotBlank(message = "Content shoudn't be blank")
    String content;
    @Positive
    Long authorId;
    @Positive
    Long projectId;
    boolean published;
    @PastOrPresent
    LocalDateTime publishedAt;
    LocalDateTime scheduledAt;
    @PastOrPresent
    LocalDateTime createdAt;
    @PastOrPresent
    LocalDateTime updatedAt;
}