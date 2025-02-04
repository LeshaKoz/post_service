package faang.school.postservice.dto.Post;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdatePostDto {
    private long id;
    private String content;
    private LocalDateTime scheduledAt;
}
