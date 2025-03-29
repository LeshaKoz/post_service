package faang.school.postservice.dto.Post;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreatePostDraftDto {
    private String content;
    private Long authorId;
    private Long projectId;
    private LocalDateTime scheduledAt;
}
