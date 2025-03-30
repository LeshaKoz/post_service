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

//@FeignClient(name = "user-service", url = "${user-service.host}:${user-service.port}")
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

    //@GetMapping("/users/{id}")
    //UserDto getUser(@PathVariable long id);

    //@GetMapping("/api/v1/users/{userId}")
    //UserDto getUser(@PathVariable long userId);

    /* @PostMapping("/users")
    List<UserDto> getUsersByIds(@RequestBody List<Long> ids);

    @GetMapping("/{followerId}/followeeeids")
    List<Long> getFolloweeIdsByFollowerId(
            @PathVariable @Min(value = 1L, message = "Follower id cannot be less than 1") long followerId);

    @GetMapping("/subscription/{followeeId}/followerids")
    List<Long> getFollowerIdsByFolloweeId(
            @PathVariable @Min(value = 1L, message = "Followee id cannot be less than 1") long followeeId);

     */
/*
@GetMapping("/api/v1/users/{userId}")
    UserResponseDto getUser(@PathVariable long userId);

    @GetMapping("/api/v1/subscriptions/followers/{followeeId}")
    List<SubscriptionUserDto> getFollowers(@PathVariable long followeeId);

    @PostMapping("/api/v1/users")
    List<UserDto> getUsersByIds(@RequestBody List<Long> ids);
 */
}
