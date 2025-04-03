package faang.school.postservice.client;

import faang.school.postservice.dto.user.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "user-service", url = "${user-service.host}:${user-service.port}/users/api/v1/users")
public interface UserServiceClient {

    @GetMapping("/{userId}")
    UserDto getUser(@PathVariable long userId);

    @GetMapping
    List<UserDto> getUsersByIds(@RequestBody List<Long> ids);

    @GetMapping("/{followeeId}/followers/count")
    int getFollowersCount(@PathVariable long followeeId);

    @GetMapping("/{followeeId}/followers")
    List<Long> getUserFollowers(
            @PathVariable long followeeId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "1000") int size
    );
}
