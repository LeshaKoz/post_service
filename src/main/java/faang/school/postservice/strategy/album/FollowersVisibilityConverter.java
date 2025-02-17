package faang.school.postservice.strategy.album;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.AlbumResponseDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.enums.Visibility;
import faang.school.postservice.exception.album.AlbumAccessDeniedException;
import faang.school.postservice.mapper.post.AlbumMapper;
import faang.school.postservice.model.Album;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import static faang.school.postservice.enums.Visibility.FOLLOWERS;

@Component
@RequiredArgsConstructor
public class FollowersVisibilityConverter implements VisibilityConverter {

    private final AlbumMapper albumMapper;
    private final UserContext userContext;
    private final UserServiceClient userServiceClient;

    @Override
    public AlbumResponseDto apply(Album album) {
        long userId = userContext.getUserId();
        List<UserDto> followers = userServiceClient.getFollowersByUserId(album.getAuthorId());
        boolean isUserFollower = followers.stream()
                .map(UserDto::id)
                .anyMatch(followerId -> followerId != null && followerId == userId);
        if (isUserFollower) {
            return albumMapper.toDto(album);
        }
        throw new AlbumAccessDeniedException(
                String.format("Access denied for user with id = %d for album with id = %d. "
                        + "User isn't follower for author with id = %d", userId, album.getId(), album.getAuthorId()));
    }

    @Override
    public Visibility getVisibility() {
        return FOLLOWERS;
    }
}
