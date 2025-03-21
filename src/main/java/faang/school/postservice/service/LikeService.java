package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.validator.CommentValidator;
import faang.school.postservice.validator.PostValidator;
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
    private final PostValidator postValidator;
    private final CommentValidator commentValidator;

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

    private List<UserDto> fetchUsersInBatches(List<Long> userIds) {
        List<UserDto> userDtos = new ArrayList<>();

        for (int i = 0; i < userIds.size(); i += BATCH_SIZE) {
            List<Long> batch = userIds.subList(i, Math.min(i + BATCH_SIZE, userIds.size()));
            try {
                userDtos.addAll(userServiceClient.getUsersByIds(batch));
            } catch (Exception e) {
                throw e;
            }
        }
        return userDtos;
    }

    private List<UserDto> getUsers(List<Long> userIds) {
        if (userIds.isEmpty()) {
            return Collections.emptyList();
        }
        return fetchUsersInBatches(userIds);
    }
}
