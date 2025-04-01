package faang.school.postservice.dto.post;


import java.time.LocalDateTime;

public record LikeDto(

        Long userId,

        Long postId,

        LocalDateTime createdAt
) {
}