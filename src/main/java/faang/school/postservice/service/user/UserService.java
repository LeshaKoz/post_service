package faang.school.postservice.service.user;

import faang.school.postservice.client.UserServiceClient;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserServiceClient userServiceClient;

    public boolean isUserExists(long userId) {
        try {
            userServiceClient.getUser(userId);
            return true;
        } catch (FeignException.FeignClientException e) {
            return false;
        }
    }
}
