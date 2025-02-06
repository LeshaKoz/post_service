package faang.school.postservice.dto.Post;

import java.time.LocalDateTime;

public class CreatePostDraftDto {
    private String content;
    private long authorId;
    private long projectId;
    private LocalDateTime scheduledAt;
}
