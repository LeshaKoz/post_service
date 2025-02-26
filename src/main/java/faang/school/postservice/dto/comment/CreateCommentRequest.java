package faang.school.postservice.dto.comment;

import lombok.Data;

@Data
public class CreateCommentRequest {
    private Long postId;
    private Long authorId;
    private String content;
}
