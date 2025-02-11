package faang.school.postservice.dto.comment;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommentReadDto {
    private Long id;
    private String content;
    private Long authorId;
    private Long postId;
    private List<Long> likeIds;
}
