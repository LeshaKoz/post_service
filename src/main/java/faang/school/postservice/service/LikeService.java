package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.UserAlreadyLikedException;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.validator.CommentValidator;
import faang.school.postservice.validator.PostValidator;
import faang.school.postservice.validator.UserValidator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LikeService {

    private static final int BATCH_SIZE = 100;

    private final UserServiceClient userServiceClient;
    private final UserContext userContext;
    private final UserValidator userValidator;
    private final PostValidator postValidator;
    private final CommentValidator commentValidator;
    private final LikeRepository likeRepository;
    private final LikeMapper likeMapper;
    private final PostMapper postMapper;

    public List<UserDto> getAllUsersWhoLikedPost(Long postId) {
        Post post = postValidator.getPostById(postId);
        List<Long> userIds = post.getLikes().stream()
                .map(Like::getUserId)
                .toList();

        return getUsers(userIds);
    }

    public List<UserDto> getAllUsersWhoLikedComment(Long commentId) {
        Comment comment = commentValidator.getCommentById(commentId);
        List<Long> userIds = comment.getLikes().stream()
                .map(Like::getUserId)
                .toList();

        return getUsers(userIds);
    }

    @Transactional
    public LikeDto likePost(Long postId) {
        Long userId = userContext.getUserId();
        userValidator.validateUserExist(userId);
        if (likeRepository.findByPostIdAndUserId(postId, userId).isPresent()) {
            throw new UserAlreadyLikedException("User with id %d is already liked post with id %d".formatted(userId, postId));
        } else {
            Like like = Like.builder().userId(userId).post(postValidator.getPostById(postId)).build();
            likeRepository.save(like);
            return likeMapper.toLikeDto(like);
        }
    }

    private List<UserDto> fetchUsersInBatches(List<Long> userIds) {
        List<UserDto> userDtos = new ArrayList<>();

        for (int i = 0; i < userIds.size(); i += BATCH_SIZE) {
            List<Long> batch = userIds.subList(i, Math.min(i + BATCH_SIZE, userIds.size()));

            userDtos.addAll(userServiceClient.getUsersByIds(batch));
        }
        return userDtos;
    }

    @Transactional
    public LikeDto removeLikeOnPost(Long postId) {
        Long userId = userContext.getUserId();
        this.userValidator.validateUserExist(userId);
        Like like = likeRepository.findByPostIdAndUserId(postId, userId).orElseThrow(() ->
                new EntityNotFoundException("Like by user with id %d on post with id %d does not exist".formatted(userId, postId)));
        this.likeRepository.delete(like);
        return this.likeMapper.toLikeDto(like);
    }

    @Transactional
    public LikeDto likeComment(Long commentId) {
        Long userId = userContext.getUserId();
        userValidator.validateUserExist(userId);
        if (likeRepository.findByCommentIdAndUserId(commentId, userId).isPresent()) {
            throw new UserAlreadyLikedException("User with id %d is already liked comment with id %d".formatted(userId, commentId));
        } else {
            Like like = Like.builder().userId(userId).comment(commentValidator.getCommentById(commentId)).build();
            likeRepository.save(like);
            return likeMapper.toLikeDto(like);
        }
    }

    private List<UserDto> getUsers(List<Long> userIds) {
        if (userIds.isEmpty()) {
            return Collections.emptyList();
        }
        return fetchUsersInBatches(userIds);
    }

    @Transactional
    public LikeDto removeLikeOnComment(Long commentId) {
        Long userId = userContext.getUserId();
        userValidator.validateUserExist(userId);
        Like like = likeRepository.findByCommentIdAndUserId(commentId, userId).orElseThrow(() ->
                new EntityNotFoundException("Like by user with id %d on comment with id %d does not exist".formatted(userId, commentId)));
        likeRepository.delete(like);
        return likeMapper.toLikeDto(like);
    }

    public PostDto countLikesPost(Long postId) {
        return postMapper.toDto(postValidator.getPostById(postId));
    }
}
