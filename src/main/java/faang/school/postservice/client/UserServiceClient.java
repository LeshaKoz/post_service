package faang.school.postservice.client;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.handler.CustomErrorDecoder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "user-service", url = "${user-service.url}",
        configuration = {FeignConfig.class, CustomErrorDecoder.class})
public interface UserServiceClient {

    @GetMapping("/users/{userId}")
    ResponseEntity<UserDto> getUser(@PathVariable long userId);

    @PostMapping("/users")
    ResponseEntity<List<UserDto>> getUsersByIds(@RequestBody List<Long> ids);
}
