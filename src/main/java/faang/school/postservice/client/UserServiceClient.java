package faang.school.postservice.client;

import faang.school.postservice.dto.user.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "user-service", url = "${user-service.host}:${user-service.port}/users/api")
public interface UserServiceClient {

    @GetMapping("/v1/users/{userId}")
    UserDto getUser(@PathVariable long userId);

    @PostMapping("/v1/users")
    List<UserDto> getUsersByIds(@RequestBody List<Long> ids);

    @GetMapping("/api/users")
    Page<UserDto> getUsers(@RequestParam int page, @RequestParam int size);

    @GetMapping("/v1/users/count")
    Long getAllUsersCount();
}
