package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.like.LikeCommentRequest;
import faang.school.postservice.dto.like.LikePostRequest;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exceptions.UserServiceConnectException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final UserServiceClient userServiceClient;
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PostService postService;
    private final CommentService commentService;


    @Transactional
    public void toggleLikePost(LikePostRequest request) {
        Post post = postRepository.findById(request.postId())
                .orElseThrow(() -> new EntityNotFoundException("Пост с id " + request.postId() + " не был найден"));
        UserDto userDto = getUserDto(request.userId());

        List<Like> likes = new ArrayList<>(post.getLikes() == null ? Collections.emptyList() : post.getLikes());
        boolean likeAlreadyExists = likes.stream().anyMatch(like -> like.getUserId() == userDto.id());

        if (likeAlreadyExists) {
            post.setLikes(likes.stream().filter(like -> like.getUserId() != userDto.id()).toList());
            likeRepository.deleteByPostIdAndUserId(post.getId(), userDto.id());
        } else {
            Like newLike = Like.builder()
                    .userId(userDto.id())
                    .post(post)
                    .createdAt(LocalDateTime.now())
                    .build();
            likeRepository.save(newLike);
            likes.add(newLike);
            post.setLikes(likes);
        }
    }

    @Transactional
    public void toggleLikeComment(LikeCommentRequest request) {
        Comment comment = commentRepository.findById(request.commentId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Комментарий с id " + request.commentId() + " не был найден"));
        UserDto userDto = getUserDto(request.userId());

        List<Like> likes = new ArrayList<>(comment.getLikes() == null ? Collections.emptyList() : comment.getLikes());
        boolean likeAlreadyExists = likes.stream().anyMatch(like -> like.getUserId() == userDto.id());

        if (likeAlreadyExists) {
            comment.setLikes(likes.stream().filter(like -> like.getUserId() != userDto.id()).toList());
            likeRepository.deleteByCommentIdAndUserId(comment.getId(), userDto.id());
        } else {
            Like newLike = Like.builder()
                    .userId(userDto.id())
                    .comment(comment)
                    .createdAt(LocalDateTime.now())
                    .build();
            likeRepository.save(newLike);
            likes.add(newLike);
            comment.setLikes(likes);
        }
    }

    private UserDto getUserDto(Long userId) {
        UserDto userDto = userServiceClient.getUser(userId);
        if (userDto == null) {
            throw new EntityNotFoundException("Пользователь с id " + userId + " не был найден");
        }
        return userDto;
    }

    @Transactional(readOnly = true)
    public List<UserDto> getLikedUsersToPost(long postId) {
        if (!postService.existsById(postId)) {
            throw new IllegalArgumentException("Post with id %s does not exist".formatted(postId));
        }

        var ids = likeRepository.findAllByPostId(postId)
                .map(Like::getUserId)
                .collect(Collectors.toList());

        try {
            return userServiceClient.getUsersByIds(ids);
        } catch (Exception e) {
            throw new UserServiceConnectException("Failed users service");
        }
    }

    @Transactional(readOnly = true)
    public List<UserDto> getLikedUsersToComment(long commentId) {
        if (!commentService.existsById(commentId)) {
            throw new IllegalArgumentException("Comment with id %s does not exist".formatted(commentId));
        }

        var ids = likeRepository.findAllByCommentId(commentId)
                .map(Like::getUserId)
                .collect(Collectors.toList());

        try {
            return userServiceClient.getUsersByIds(ids);
        } catch (Exception e) {
            throw new UserServiceConnectException("Failed users service");
        }
    }
}
