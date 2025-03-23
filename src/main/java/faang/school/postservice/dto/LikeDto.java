package faang.school.postservice.dto;

public record LikeDto(
        Long userId,
        Long postId,
        Long commentId
) {
}
