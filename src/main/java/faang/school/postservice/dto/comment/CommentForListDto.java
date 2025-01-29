package faang.school.postservice.dto.comment;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentForListDto {
    private Long id;
    private Long authorId;
    private String content;
    private LocalDateTime createdAt;
}
