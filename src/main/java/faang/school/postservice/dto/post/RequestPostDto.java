package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestPostDto {
    private Long id;

    @NotBlank(message = "Content shoudn't be blank")
    private String content;

    @Positive
    private Long authorId;

    @Positive
    private Long projectId;

    @PastOrPresent
    private LocalDateTime publishedAt;

    private LocalDateTime scheduledAt;

    @PastOrPresent
    private LocalDateTime createdAt;

    @PastOrPresent
    private LocalDateTime updatedAt;
}