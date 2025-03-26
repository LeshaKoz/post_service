package faang.school.postservice.dto.post;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostRedisDto {
    private Long id;
    private String content;
    private Long authorId;
    private LocalDateTime publishedAt;
}