package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class LikeService {

    private PostRepository postRepository;
    private CommentRepository commentRepository;
    private UserContext userContext;
    private UserServiceClient userServiceClient;

    private static final String ERROR_POST_DOES_NOT_EXIST = "Post with id={} doesn't exist";
    private static final String ERROR_COMMENT_DOES_NOT_EXIST = "Comment with id={} doesn't exist";
    private static final String ERROR_COMMENT_DOES_NOT_BELONG_TO_POST = "Comment with id={} doesn't belong to post with id={}";
    private static final String USER_LIKE_POST_ATTEMPT = "User with id={} wants to set like={} to post with id={}";
    private static final String USER_LIKES_POST = "User with id={} likes post with id={}";
    private static final String USER_LIKES_COMMENT = "User likes comment: userId={}, commentId={}, postId={}";
    private static final String USER_TAKES_LIKE_AWAY_FROM_POST = "User takes like away from post with id={}";
    private static final String USER_TAKES_LIKE_AWAY_FROM_COMMENT = "User takes like away from comment: userId={}, commentId={}, postId={}";
    private static final String WARN_USER_SETS_CURRENT_LIKE_STATUS = "User like action repeats current like status: userId={}, commentId={}, postId={}";
    private static final String ERROR_USER_IS_NOT_PRESENTED_IN_DB = "User is not presented in DB: userId={}";

    public void likePost(boolean like, Long postId) {
        log.info(USER_LIKE_POST_ATTEMPT, userContext.getUserId(), like, postId);
        Post post = getPost(postId);
        UserDto userDto = getUserFromUserService(userContext.getUserId());

        List<Like> allLikesOfPost = post.getLikes();
        List<Like> userLikesOfPost = allLikesOfPost.stream()
                .filter(l -> l.getUserId().equals(userContext.getUserId()))
                .toList();
        if (userLikesOfPost.isEmpty() && like) {
            Like newLike = new Like();
            newLike.setPost(post);
            newLike.setUserId(userContext.getUserId());
            allLikesOfPost.add(newLike);
            post.setLikes(allLikesOfPost);
            postRepository.save(post);
            log.info(USER_LIKES_POST, userContext.getUserId(), postId);
        } else if ((!userLikesOfPost.isEmpty()) && (!like)) {
            allLikesOfPost.remove(userLikesOfPost.get(0));
            post.setLikes(allLikesOfPost);
            postRepository.save(post);
            log.info(USER_TAKES_LIKE_AWAY_FROM_POST, postId);
        } else {
            log.info(WARN_USER_SETS_CURRENT_LIKE_STATUS, userContext.getUserId(), "N/A", postId);
        }
    }

    public void likeComment(boolean like, Long commentId, Long postId) {
        Comment comment = getComment(commentId, postId);
        UserDto userDto = getUserFromUserService(userContext.getUserId());

        List<Like> allLikesOfComment = comment.getLikes();
        List<Like> userLikesOfComment = allLikesOfComment.stream()
                .filter(l -> l.getUserId().equals(userContext.getUserId()))
                .toList();
        if (userLikesOfComment.isEmpty() && like) {
            Like newLike = new Like();
            newLike.setPost(comment.getPost());
            newLike.setUserId(userContext.getUserId());
            newLike.setComment(comment);
            allLikesOfComment.add(newLike);
            comment.setLikes(allLikesOfComment);
            commentRepository.save(comment);
            log.info(USER_LIKES_COMMENT, userContext.getUserId(), commentId, postId);
        } else if ((!userLikesOfComment.isEmpty()) && (!like)) {
            allLikesOfComment.remove(userLikesOfComment.get(0));
            comment.setLikes(allLikesOfComment);
            commentRepository.save(comment);
            log.info(USER_TAKES_LIKE_AWAY_FROM_COMMENT, userContext.getUserId(), commentId, postId);
        } else {
            log.info(WARN_USER_SETS_CURRENT_LIKE_STATUS, userContext.getUserId(), commentId, postId);
        }
    }

    private Post getPost(Long postId) {
        Optional<Post> optional = postRepository.findById(postId);
        if (optional.isPresent()) {
            return optional.get();
        } else {
            log.error(ERROR_POST_DOES_NOT_EXIST, postId);
            throw new DataValidationException(ERROR_POST_DOES_NOT_EXIST);
        }
    }

    private Comment getComment(Long commentId, Long postId) {
        Optional<Comment> optionalComment = commentRepository.findById(commentId);
        if (optionalComment.isPresent()) {
            Comment comment = optionalComment.get();
            if (comment.getPost().getId().equals(postId)) {
                return comment;
            } else {
                log.error(ERROR_COMMENT_DOES_NOT_BELONG_TO_POST, commentId, postId);
                throw new DataValidationException(ERROR_COMMENT_DOES_NOT_BELONG_TO_POST);
            }
        } else {
            log.error(ERROR_COMMENT_DOES_NOT_EXIST, postId);
            throw new DataValidationException(ERROR_POST_DOES_NOT_EXIST);
        }
    }

    private UserDto getUserFromUserService(Long userId) {
        UserDto userDto = userServiceClient.getUser(userId);
        if (userDto.id().equals(userId)) {
            return userDto;
        } else {
            log.error(ERROR_USER_IS_NOT_PRESENTED_IN_DB, userId);
            throw new DataValidationException(ERROR_USER_IS_NOT_PRESENTED_IN_DB);
        }
    }
}
