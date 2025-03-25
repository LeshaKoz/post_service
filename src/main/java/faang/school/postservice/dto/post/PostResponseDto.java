package faang.school.postservice.dto.post;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public final class PostResponseDto implements Serializable {

    private Long id;

    private String content;

    private Long authorId;

    private Long projectId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
