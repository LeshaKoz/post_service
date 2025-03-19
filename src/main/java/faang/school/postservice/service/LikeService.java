package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.exception.ConcurrentLikeException;
import faang.school.postservice.exception.DuplicateEntityException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.like.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LikeService {

    private static final String LIKE_ENTITY_NAME = "like";
    private static final String POST_ENTITY_NAME = "post";
    private static final String COMMENT_ENTITY_NAME = "comment";
    private static final String USER_ENTITY_NAME = "user";
    private static final String NOT_FOUND_ENTITY_MESSAGE = "%s with id: %d not found";
    private static final String NOT_FOUND_SUB_ENTITY_MESSAGE = "%s on %s with id: %d not found";

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserServiceClient userServiceClient;
    private final LikeMapper likeMapper;

    public void putLikeOnPost(Long postId, LikeDto likeDto) {
        validateEntityId(postId);
        Long userId = extractIdOnLikeDto(likeDto);
        Post post = postRepository.findById(postId).orElseThrow(() ->
                new EntityNotFoundException(NOT_FOUND_ENTITY_MESSAGE, POST_ENTITY_NAME, postId));
        checkUserExists(userId);

        Optional<Like> foundLikeOnPost = likeRepository.findByPostIdAndUserId(postId, userId);
        checkLikeExists(foundLikeOnPost, POST_ENTITY_NAME, postId);

        boolean isUserNotPutLike = post.getComments().stream()
                .allMatch(comment -> comment.getLikes().stream()
                        .noneMatch(like -> like.getUserId().equals(userId)));
        checkUserPuttingLike(isUserNotPutLike, COMMENT_ENTITY_NAME, POST_ENTITY_NAME, postId);

        addLikeOnDatabase(likeDto, post, null);
        printMessageAddLike(postId);
    }

    public void removeLikeAtPost(Long postId, LikeDto likeDto) {
        validateEntityId(postId);
        Long userId = extractIdOnLikeDto(likeDto);
        checkUserExists(userId);

        if (likeRepository.findByPostIdAndUserId(postId, userId).isEmpty()) {
            throw new EntityNotFoundException(NOT_FOUND_SUB_ENTITY_MESSAGE,
                    LIKE_ENTITY_NAME, POST_ENTITY_NAME, postId);
        }
        likeRepository.deleteByPostIdAndUserId(postId, userId);
        printMessageRemoveLike(postId);
    }

    public void putLikeOnComment(Long commentId, LikeDto likeDto) {
        validateEntityId(commentId);
        Long userId = extractIdOnLikeDto(likeDto);
        Comment comment = commentRepository.findById(commentId).orElseThrow(() ->
                new EntityNotFoundException(NOT_FOUND_ENTITY_MESSAGE, COMMENT_ENTITY_NAME, commentId));
        checkUserExists(userId);

        Optional<Like> foundLikeOnComment = likeRepository.findByCommentIdAndUserId(commentId, userId);
        checkLikeExists(foundLikeOnComment, COMMENT_ENTITY_NAME, commentId);

        boolean isUserNotPutLike = comment.getPost().getLikes().stream()
                .noneMatch(like -> like.getUserId().equals(userId));
        checkUserPuttingLike(isUserNotPutLike, POST_ENTITY_NAME, COMMENT_ENTITY_NAME, commentId);

        addLikeOnDatabase(likeDto, null, comment);
        printMessageAddLike(commentId);
    }

    public void removeLikeAtComment(Long commentId, LikeDto likeDto) {
        validateEntityId(commentId);
        Long userId = extractIdOnLikeDto(likeDto);
        checkUserExists(userId);

        if (likeRepository.findByCommentIdAndUserId(commentId, userId).isEmpty()) {
            throw new EntityNotFoundException(NOT_FOUND_SUB_ENTITY_MESSAGE,
                    LIKE_ENTITY_NAME, COMMENT_ENTITY_NAME, commentId);
        }
        likeRepository.deleteByCommentIdAndUserId(commentId, likeDto.userId());
        printMessageRemoveLike(commentId);
    }

    private void validateEntityId(Long entityId) {
        Objects.requireNonNull(entityId, "Invalid like target id value");
    }

    private void printMessageAddLike(Long postId) {
        log.debug("Added like on {} with id: {}", POST_ENTITY_NAME, postId);
    }

    private void printMessageRemoveLike(Long commentId) {
        log.debug("Removed like on {} with id: {}", COMMENT_ENTITY_NAME, commentId);
    }

    private void checkUserExists(Long userId) {
        if (userServiceClient.getUser(userId) == null) {
            throw new EntityNotFoundException("%s with id: %d not found", USER_ENTITY_NAME, userId);
        }
    }

    private void checkLikeExists(Optional<Like> like, String entityName, Long entityId) {
        if (like.isPresent()) {
            throw new DuplicateEntityException("%s already exists on %s with id %d",
                    LIKE_ENTITY_NAME, entityName, entityId);
        }
    }

    private void checkUserPuttingLike(boolean isUserNotPutLike, String subEntityName,
                                      String entityName, Long entityId) {
        if (!isUserNotPutLike) {
            throw new ConcurrentLikeException(
                    "Like on %s this %s with id: %d already exists", subEntityName, entityName, entityId);
        }
    }

    private void addLikeOnDatabase(LikeDto likeDto, Post post, Comment comment) {
        Like like = likeMapper.dtoToEntity(likeDto);
        like.setPost(post);
        like.setComment(comment);
        likeRepository.save(like);
    }

    private Long extractIdOnLikeDto(LikeDto likeDto) {
        return likeDto.userId();
    }
}
