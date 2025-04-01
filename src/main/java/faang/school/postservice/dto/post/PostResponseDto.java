package faang.school.postservice.dto.post;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PostResponseDto(

        long id,

        String content,

        long authorId,

        long projectId,

        int likesCount,

        int commentsCount,

        int viewsCount,

        LocalDateTime createdAt,

        LocalDateTime updatedAt
) {

}
