package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.EntityNotFoundException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserServiceClient userServiceClient;
    private final UserContext userContext;

    public UserDto getUserDtoById(long userId) {
        try {
            userContext.setUserId(userId);
            return userServiceClient.getUser(userId);
        } catch (FeignException.NotFound e) {
            throw new EntityNotFoundException("Пользователь с ID " + userId + " не найден " + e.getMessage());
        }
    }

    public List<Long> getUserFollowers(long foloweeId, int page, int size) {
        try {
            userContext.setUserId(foloweeId);
            return userServiceClient.getUserFollowers(foloweeId, page, size);
        } catch (FeignException.NotFound e) {
            throw new EntityNotFoundException("Пользователь с ID " + foloweeId + " не найден " + e.getMessage());
        }
    }

    public int getUserFollowersCount(long foloweeId) {
        try {
            userContext.setUserId(foloweeId);
            return userServiceClient.getFollowersCount(foloweeId);
        } catch (FeignException.NotFound e) {
            throw new EntityNotFoundException("Пользователь с ID " + foloweeId + " не найден " + e.getMessage());
        }
    }
}
