package faang.school.postservice.client;

import faang.school.postservice.dto.user.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class UserWebClient {
    private final WebClient userClient;
    private final RestTemplate restTemplate;

    public UserDto getUserById(Long userId) {
        String url = "http://localhost:8080/users/{userId}";
        return restTemplate.getForObject(url, UserDto.class, userId);
    }
}
