package faang.school.postservice.dto.like;

public record LikePostEvent(Long authorId,
                            Long likerId,
                            Long postId) {
}
