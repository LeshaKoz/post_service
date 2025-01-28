package faang.school.postservice.service.like;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.service.comment.CommentService;
import faang.school.postservice.service.post.PostService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final UserServiceClient userServiceClient;
    private final PostService postService;
    private final CommentService commentService;

    @Transactional
    public Like saveLikePost(long postId, Long userId) {
        checkExistUserId(userId);
        Post post = postService.getPostById(postId);
        checkExistLikeForPost(postId, userId);
        Like like = buildLikePost(post, userId);

        return likeRepository.save(like);
    }

    @Transactional
    public Like saveLikeComment(long commentId, long userId) {
        checkExistUserId(userId);
        Comment comment = commentService.getComment(commentId);
        checkExistLikeForComment(commentId, userId);
        Like like = buildLikeComment(comment, userId);

        return likeRepository.save(like);
    }

    @Transactional
    public void deleteLikePost(long postId, Long userId) {
        checkExistUserId(userId);
        if (likeRepository.findByPostIdAndUserId(postId, userId).isEmpty()) {
            throw new EntityExistsException(String.format("Like not exists for postId: %d", postId));
        }
        likeRepository.deleteByPostIdAndUserId(postId, userId);
    }

    @Transactional
    public void deleteLikeComment(long commentId, Long userId) {
        checkExistUserId(userId);
        if (likeRepository.findByCommentIdAndUserId(commentId, userId).isEmpty()) {
            throw new EntityExistsException(String.format("Like not exists for commentId: %d", commentId));
        }
        likeRepository.deleteByCommentIdAndUserId(commentId, userId);
    }

    public Long countLikesPost(Long postId) {
        Post post = postService.getPostById(postId);
        if (!post.isPublished()) {
            throw new IllegalArgumentException("Post is not published");
        }
        return post.getLikes().stream()
            .filter(Objects::nonNull)
            .count();
    }

    public List<String> likesPost(Long postId) {
        Post post = postService.getPostById(postId);
        if (post == null) {
            throw new EntityNotFoundException("Post not found with id: " + postId);
        }

        return post.getLikes().stream()
            .filter(Objects::nonNull)
            .map(Like::getUserId)
            .filter(Objects::nonNull)
            .map(userServiceClient::getUser)
            .filter(Objects::nonNull)
            .map(UserDto::username)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    public List<String> likesComment(Long commentId) {
        Comment comment = commentService.getComment(commentId);
        if (comment == null) {
            throw new EntityNotFoundException("Comment not found with id: " + commentId);
        }

        return comment.getLikes().stream()
            .filter(Objects::nonNull)
            .map(Like::getUserId)
            .filter(Objects::nonNull)
            .map(userServiceClient::getUser)
            .filter(Objects::nonNull)
            .map(UserDto::username)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    private void checkExistLikeForPost(long postId, long userId) {
        if (likeRepository.findByPostIdAndUserId(postId, userId).isPresent()) {
            throw new EntityExistsException(
                String.format("Like already exists for postId: %d", postId));
        }
    }

    private void checkExistLikeForComment(long commentId, long userId) {
        if (likeRepository.findByCommentIdAndUserId(commentId, userId).isPresent()) {
            throw new EntityExistsException(
                String.format("Like already exists for commentId: %d", commentId));
        }
    }

    private void checkExistUserId(Long userId) {
        log.info("check that the user exists in the system with userId {}", userId);
        if (!userServiceClient.getUser(userId).id().equals(userId)) {
            throw new EntityNotFoundException("User not found with userId = " + userId);
        }
    }

    private Like buildLikePost(Post post, long userId) {
        return Like.builder()
            .post(post)
            .userId(userId)
            .build();
    }

    private Like buildLikeComment(Comment comment, long userId) {
        return Like.builder()
            .comment(comment)
            .userId(userId)
            .build();
    }
}
