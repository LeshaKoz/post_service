package faang.school.postservice.dto.comment;

import lombok.Builder;

@Builder
public record CommentRequestDto(
        Long authorId,
        Long postId,
        String content
) { }
