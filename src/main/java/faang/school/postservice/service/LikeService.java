package faang.school.postservice.service;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.like.LikeMapper;
import faang.school.postservice.model.Like;
import faang.school.postservice.repository.ad.LikeRepository;
import faang.school.postservice.validation.LikeValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final LikeMapper likeMapper;
    private final LikeValidator likeValidator;

    public void likePost(Long postId, LikeDto likeDto) {
        likeValidator.validateUserExists(likeDto.getUserId());
        likeValidator.validatePostExists(postId);

        if (likeRepository.findByPostIdAndUserId(postId, likeDto.getUserId()).isPresent()) {
            throw new DataValidationException("User already liked this post.");
        }

        saveLike(likeMapper.toLikePost(likeDto, postId));
    }

    @Transactional
    public void unlikePost(Long postId, LikeDto likeDto) {
        likeValidator.validateUserExists(likeDto.getUserId());
        likeRepository.deleteByPostIdAndUserId(postId, likeDto.getUserId());
    }

    public void likeComment(Long commentId, LikeDto likeDto) {
        likeValidator.validateUserExists(likeDto.getUserId());
        likeValidator.validateCommentExists(commentId);

        if (likeRepository.findByCommentIdAndUserId(commentId, likeDto.getUserId()).isPresent()) {
            throw new DataValidationException("User already liked this comment.");
        }

        saveLike(likeMapper.toLikeComment(likeDto, commentId));
    }

    @Transactional
    public void unlikeComment(Long commentId, LikeDto likeDto) {
        likeValidator.validateUserExists(likeDto.getUserId());
        likeRepository.deleteByCommentIdAndUserId(commentId, likeDto.getUserId());
    }

    private void saveLike(Like like) {
        likeRepository.save(like);
    }
}