package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserServiceClient userServiceClient;

    public UserDto getUser(long userId) {
        try {
            return userServiceClient.getUser(userId);
        } catch (FeignException.FeignClientException ex) {
            throw new EntityNotFoundException(String.format("Пользователь с id=%d не найден", userId));
        }
    }

    public boolean isUserExists(long userId) {
        try {
            userServiceClient.getUser(userId);
            return true;
        } catch (FeignException.FeignClientException ex) {
            return false;
        }
    }
}
