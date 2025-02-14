package faang.school.postservice.service.like;


import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.post.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.like.LikeRepository;
import faang.school.postservice.repository.post.PostRepository;
import faang.school.postservice.validator.LikeValidator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class LikeServiceImpl {
    private final LikeValidator likeValidator;
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserServiceClient userServiceClient;

    public LikeDto addLikeToPost(Long userId, Long postId) {
        userServiceClient.getUser(userId);
        likeValidator.validateLikeExists(new LikeDto(null, userId, postId));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new DataValidationException("Post not found"));

        Like like = new Like();
        like.setUserId(userId);
        like.setPost(post);
        likeRepository.save(like);

        return new LikeDto(like.getId(), userId, postId);
    }

    public void removeLikeFromPost(Long postId, Long userId) {
        userServiceClient.getUser(userId);
        Like like = likeRepository.findByPostIdAndUserId(postId, userId)
                .orElseThrow(() -> new DataValidationException("Like not found"));
        likeRepository.delete(like);
    }

    public LikeDto addLikeToComment(Long userId, Long commentId) {
        userServiceClient.getUser(userId);
        LikeDto likeDto = new LikeDto(null, userId, commentId);
        likeValidator.validateLikeExists(likeDto);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new DataValidationException("Comment not found"));
        Like like = new Like();
        like.setUserId(likeDto.getUserId());
        like.setComment(comment);
        likeRepository.save(like);
        return new LikeDto(like.getId(), userId, commentId);
    }

    public void removeLikeFromComment(Long commentId, Long userId) {
        userServiceClient.getUser(userId);
        Like like = likeRepository.findByCommentIdAndUserId(commentId, userId)
                .orElseThrow(() -> new DataValidationException("Like not found"));
        likeRepository.delete(like);
    }

    public long getPostLikeCount(Long postId) {
        return likeRepository.findByPostId(postId).size();
    }
}
