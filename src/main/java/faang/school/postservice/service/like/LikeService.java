package faang.school.postservice.service.like;

import faang.school.postservice.dto.like.LikeDto;

public interface LikeService {
    LikeDto addLikeToPost(Long userId, Long postId);
    void removeLikeFromPost(Long postId, Long userId);
    LikeDto addLikeToComment(Long userId, Long commentId);
    void removeLikeFromComment(Long commentId, Long userId);
    long getPostLikeCount(Long postId);
}
