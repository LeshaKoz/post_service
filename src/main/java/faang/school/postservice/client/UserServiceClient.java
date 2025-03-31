package faang.school.postservice.client;

import faang.school.postservice.dto.user.SubscriptionUserDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.dto.user.UserResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "user-service", url = "${user-service.host}:${user-service.port}${user-service.version}")
public interface UserServiceClient {

    @GetMapping("/users/{userId}")
    UserResponseDto getUser(@PathVariable long userId);

    @PostMapping("/users")
    List<UserDto> getUsersByIds(@RequestBody List<Long> ids);

    @GetMapping("/subscriptions/followers/{followeeId}")
    List<SubscriptionUserDto> getFollowers(@PathVariable long followeeId);

    @PostMapping("/follow")
    void followUser(@RequestParam long followerId, @RequestParam long followeeId);
}
