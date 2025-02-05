package faang.school.postservice.dto.post;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@RequiredArgsConstructor
public class PostResponseDto implements Serializable {

    private final Long id;

    private final String content;

    private final Long authorId;

    private final Long projectId;

    private final LocalDateTime createdAt;

    private final LocalDateTime updatedAt;
}
