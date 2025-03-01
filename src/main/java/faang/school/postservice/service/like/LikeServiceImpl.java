package faang.school.postservice.service.like;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final UserContext userContext;
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikeServiceValidator likeServiceValidator;

    @Override
    @Transactional
    public void createLikeForPost(Long postId) {
        final long userId = getUserId();
        log.info("Creating a user id = {}, like for a post = {}", userId, postId);

        likeServiceValidator.validatePostLiked(postId, userId);
        Post post = getPost(postId);
        final Like like = new Like();
        like.setUserId(userId);
        like.setPost(post);

        final Like savedLike = likeRepository.save(like);
        log.info("UserId = {} successfully liked postId = {} with {} ", userId, postId, savedLike);
    }

    @Override
    @Transactional
    public void createLikeForComment(Long commentId) {
        final long userId = getUserId();
        log.info("Creating like with userId = {} like for a commentId = {}", userId, commentId);

        likeServiceValidator.validateCommentLiked(commentId, userId);
        Comment comment = getComment(commentId);

        final Like like = new Like();
        like.setUserId(userId);
        like.setComment(comment);
        like.setPost(comment.getPost());

        final Like savedLike = likeRepository.save(like);
        log.info("UserId = {} successfully liked commentId = {} with {}", userId, commentId, savedLike);
    }

    @Override
    @Transactional
    public void deleteLikeFromPost(long postId) {
        final long userId = getUserId();
        likeRepository.deleteByPostIdAndUserId(postId, userId);
        log.info("Successfully deleted like for postId = {} by userId = {}", postId, userId);
    }

    @Override
    @Transactional
    public void deleteLikeFromComment(long commentId) {
        final long userId = getUserId();
        likeRepository.deleteByCommentIdAndUserId(commentId, userId);
        log.info("Successfully deleted like for commentId = {} by userId = {}", commentId, userId);
    }

    private Post getPost(Long postId) {
        return postRepository.findById(postId).orElseThrow(() -> {
            log.error("Post with ID = {} does not exist", postId);
            return new EntityNotFoundException(String.format("Post id = %s, does not exist!", postId));
        });
    }

    private Comment getComment(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(() -> {
            log.error("Comment with ID = {} does not exist", commentId);
            return new EntityNotFoundException(String.format("Comment id = %s, does not exist!",commentId));
        });
    }

    private long getUserId() {
        return userContext.getUserId();
    }
}