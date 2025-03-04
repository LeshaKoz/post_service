package faang.school.postservice.dto.comment;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentCreateEventDto {
    private final String content;
    private final Long postId;
    private final Long authorId;
    private final LocalDateTime date;
}
