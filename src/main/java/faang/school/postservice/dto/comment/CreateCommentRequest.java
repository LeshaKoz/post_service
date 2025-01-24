package faang.school.postservice.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateCommentRequest {
    private Long postId;
    private Long authorId;
    private String content;
}
