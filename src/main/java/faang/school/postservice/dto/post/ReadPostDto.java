package faang.school.postservice.dto.post;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReadPostDto {
    private Long id;
    private Long authorId;
    private Long projectId;
    private LocalDateTime publishedDate;
    private Boolean published;
    private String content;
    private Boolean deleted;
}