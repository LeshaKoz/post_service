package faang.school.postservice.client;

import faang.school.postservice.dto.user.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "user-service", url = "${user-service.host}:${user-service.port}")
public interface UserServiceClient {
    @GetMapping("${user-service.api-prefix}/user/{userId}")
    UserDto getUser(@PathVariable long userId);

    @PostMapping("${user-service.api-prefix}/user")
    List<UserDto> getUsersByIds(@RequestBody List<Long> ids);

    @GetMapping("${user-service.api-prefix}/subscription/{followerId}/follow/{authorId}")
    boolean isFollow(@PathVariable long followerId, @PathVariable long authorId);
}
