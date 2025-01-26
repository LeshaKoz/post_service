package faang.school.postservice.dto.comment;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record CommentResponseDto(
        Long id,
        String content,
        Long authorId,
        List<Long> likeIds,
        Long postId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {  }
