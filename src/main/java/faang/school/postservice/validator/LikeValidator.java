package faang.school.postservice.validator;


import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.repository.like.LikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LikeValidator {
    private final LikeRepository likeRepository;

    public void validateLikeExists(LikeDto likeDto) {

        if (likeDto.getUserId() == null) {
            throw new DataValidationException("User ID cannot be null");
        }
        if (likeDto.getPostId() == null && likeDto.getCommentId() == null) {
            throw new DataValidationException("Either postId or commentId must be provided");
        }
        if (likeDto.getPostId() != null) {
            if (likeRepository.findByPostIdAndUserId(likeDto.getPostId(), likeDto.getUserId()).isPresent()) {
                throw new DataValidationException("User already liked this post");
            }
        } else if (likeDto.getCommentId() != null) {
            if (likeRepository.findByCommentIdAndUserId(likeDto.getCommentId(), likeDto.getUserId()).isPresent()) {
                throw new DataValidationException("User already liked this comment");
            }
        }
        if (likeDto.getPostId() != null && likeRepository.findByCommentIdAndUserId(likeDto.getPostId(),
                likeDto.getUserId()).isPresent()) {
            throw new DataValidationException("User already liked a comment, cannot like a post at the same time");
        }
        if (likeDto.getCommentId() != null && likeRepository.findByCommentIdAndUserId(likeDto.getCommentId(),
                likeDto.getUserId()).isPresent()) {
            throw new DataValidationException("User already liked a post, cannot like a comment at the same time");
        }
    }
}