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
public class LikeService {
    private final LikeRepository likeRepository;
    private final UserServiceClient userServiceClient;
    private static final int BATCH_SIZE = 100;

    @Transactional
    public List<UserDto> getUserLikedPost(long postId) {
        List<Like> likes = likeRepository.findByPostId(postId);
        List<Long> userIds = likes.stream()
                .map(Like::getUserId)
                .toList();

        List<UserDto> result = new ArrayList<>();
        for (int i = 0; i < userIds.size(); i += BATCH_SIZE) {
            int end = Math.min(i + BATCH_SIZE, userIds.size());
            List<Long> batch = userIds.subList(i, end);
            result.addAll(userServiceClient.getUsersByIds(batch));
        }
        return result;
    }
}
