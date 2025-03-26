package faang.school.postservice.service.like;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Like;
import faang.school.postservice.repository.LikeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {
    private final LikeRepository likeRepository;
    private final UserServiceClient userServiceClient;
    private static final int BATCH_SIZE = 100;

    @Transactional
    public List<UserDto> getUserLikedPost(long postId) {
        List<Like> likes = likeRepository.findByPostId(postId);
        List<Long> userIds = likes.stream()
                .map(Like::getUserId)
                .toList();
        return getUserDtosInBatches(userIds);
    }

    @Transactional
    public List<UserDto> getUserLikedComment(long commentId) {
        List<Like> likes = likeRepository.findByCommentId(commentId);
        List<Long> userIds = likes.stream()
                .map(Like::getUserId)
                .toList();
        return getUserDtosInBatches(userIds);
    }

    private List<UserDto> getUserDtosInBatches(List<Long> userIds) {
        List<UserDto> result = new ArrayList<>();
        for (int i = 0; i < userIds.size(); i += BATCH_SIZE) {
            int endIndex = Math.min(i + BATCH_SIZE, userIds.size());
            List<Long> batch = userIds.subList(i, endIndex);
            result.addAll(userServiceClient.getUsersByIds(batch));
        }
        return result;
    }
}
