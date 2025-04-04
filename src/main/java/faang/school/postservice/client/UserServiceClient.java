package faang.school.postservice.client;

import faang.school.postservice.dto.user.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "user-service", url = "${user-service.host}:${user-service.port}/${user-service.api-prefix}")
public interface UserServiceClient {
    @GetMapping("/user/{userId}")
    UserDto getUser(@PathVariable long userId);

    @PostMapping("/user")
    List<UserDto> getUsersByIds(@RequestBody List<Long> ids);

    @GetMapping("/subscription/{followerId}/follow/{authorId}")
    boolean isFollow(@PathVariable long followerId, @PathVariable long authorId);

    @GetMapping("/user/{userId}/followers")
    List<Long> getFollowersIdsByUserId(@PathVariable long userId);
}
