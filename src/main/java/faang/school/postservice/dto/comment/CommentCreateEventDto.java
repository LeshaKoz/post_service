package faang.school.postservice.dto.comment;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentCreateEventDto {
    private final String content;
    private final Long postId;
    private final Long authorId;
}
