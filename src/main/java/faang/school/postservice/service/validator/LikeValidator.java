package faang.school.postservice.service.validator;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.exception.BusinessException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class LikeValidator {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final LikeRepository likeRepository;

    @Transactional
    public void validateLikeCreationParams(LikeDto dto) {
        userService.getUserDtoById(dto.userId());
        checkLikeFieldsDto(dto);

        if (dto.postId() != null) {
            var post = findPostById(dto.postId());
            verifyPostLikeExists(dto);

        } else {
            var comment = findCommentById(dto.commentId());
            verifyCommentLikeExists(dto);
        }

    }

    public void checkLikeFieldsDto(LikeDto dto) {
        if (dto.postId() != null && dto.commentId() != null) {
            throw new BusinessException("Лайк может быть связан только с одним объектом: постом или комментарием.");
        }
    }

    private void verifyPostLikeExists(LikeDto dto) {
        Like like = likeRepository.findLikeByPostIdAndUserId(dto.postId(), dto.userId()).orElse(null);
        if (like != null) {
            throw new BusinessException("Нельзя повторно ставить лайк на пост с ID " + dto.postId());
        }
    }

    private void verifyCommentLikeExists(LikeDto dto) {
        Like like = likeRepository.findLikeByCommentIdAndUserId(dto.commentId(), dto.userId()).orElse(null);
        if (like != null) {
            throw new BusinessException("Нельзя повторно ставить лайк на комментарий с ID " + dto.commentId());
        }
    }

    public Post findPostById(long postId) {
        return postRepository
                .findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Пост с ID " + postId + " не найден"));
    }

    public Comment findCommentById(long commentId) {
        return commentRepository
                .findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Комментарий с ID " + commentId + " не найден"));
    }

    @Transactional
    public void checkLikeBeforeDelete(LikeDto dto) {
        checkLikeFieldsDto(dto);
        if (dto.postId() != null) {
            likeRepository.findLikeByPostIdAndUserId(dto.postId(), dto.userId())
                    .orElseThrow(() -> new EntityNotFoundException("Лайк к посту с ID " + dto.postId() + " не найден"));
        } else {
            likeRepository.findLikeByCommentIdAndUserId(dto.commentId(), dto.userId())
                    .orElseThrow(() -> new EntityNotFoundException("Лайк к комментарию с ID " + dto.commentId() + " не найден"));
        }
    }

}
