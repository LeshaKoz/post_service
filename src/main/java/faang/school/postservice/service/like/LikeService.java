package faang.school.postservice.service.like;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Like;
import faang.school.postservice.repository.LikeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final UserServiceClient userServiceClient;

    @Transactional
    public List<UserDto> getUserLikedPost(long postId) {
        List<Like> likes = likeRepository.findByPostId(postId);
        List<Long> userIds = likes.stream()
                .map(Like::getUserId)
                .toList();
        return userServiceClient.getUsersByIds(userIds);
    }
}
