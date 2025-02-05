package faang.school.postservice.gateway;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.ExternalServiceValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserServiceGateway {
    private final UserServiceClient userServiceClient;

    public UserDto getUser(Long userId) {
        ResponseEntity<UserDto> response = userServiceClient.getUser(userId);
        if (response.getBody() == null) {
            throw new ExternalServiceValidationException("Empty response from UserService for ID: " + userId);
        }
        return response.getBody();
    }
}