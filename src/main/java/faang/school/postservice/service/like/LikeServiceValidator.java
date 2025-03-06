package faang.school.postservice.service.like;

import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class LikeServiceValidator {

    private final LikeRepository likeRepository;

    public void validatePostLiked(long postId, long userId) {
        if (isPostLikedByUser(postId, userId)) {
            log.error("User id = {} cannot like with post id = {}, already liked", userId, postId);
            throw new DataValidationException("You already liked this post!");
        }
    }

    public void validateCommentLiked(long commentId, long userId) {
        if (isCommentLikedByUser(commentId, userId)) {
            log.error("UserId = {} cannot like with commentId = {}, already liked", userId, commentId);
            throw new DataValidationException("You already liked this comment!");
        }
    }

    private boolean isPostLikedByUser(long postId, long userId) {
        log.debug("Searching existent like with post id = {}, user id = {}", postId, userId);
        return likeRepository.findByPostIdAndUserId(postId, userId).isPresent();
    }

    private boolean isCommentLikedByUser(long commentId, long userId) {
        log.debug("Searching existent like with comment id = {}, user id = {}", commentId, userId);
        return likeRepository.findByCommentIdAndUserId(commentId, userId).isPresent();
    }
}
