package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.EntityNotFoundException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
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

    public List<Long> getFollowerIds(Long authorId) {
        try{
            userContext.setUserId(authorId);
            List<Long> followerIds = userServiceClient.getFollowerIds(authorId);
            return followerIds != null ? followerIds : Collections.emptyList();
        } catch (FeignException e) {
            log.error("Ошибка при получении подписчиков автора с ID {}: {}", authorId, e.getMessage());
            throw new EntityNotFoundException("Ошибка при получении подписчиков автора с ID " + authorId);
        }
    }
}
