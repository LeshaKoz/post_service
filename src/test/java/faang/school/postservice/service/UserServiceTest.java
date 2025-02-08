package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private UserService userService;

    @Test
    public void testGetUserIfUserExists() {
        UserDto userDto = UserDto.builder().build();
        when(userServiceClient.getUser(anyLong())).thenReturn(userDto);

        assertNotNull(userService.getUser(anyLong()));
    }

    @Test
    public void testGetUserIfUserNotExists() {
        when(userServiceClient.getUser(anyLong())).thenThrow(FeignException.FeignClientException.class);

        assertThrows(EntityNotFoundException.class, () -> userService.getUser(anyLong()));
    }

    @Test
    public void testIsUserExistsIfUserExists() {
        UserDto userDto = UserDto.builder().build();
        when(userServiceClient.getUser(anyLong())).thenReturn(userDto);

        assertTrue(userService.isUserExists(anyLong()));
    }

    @Test
    public void testIsUserExistsIfUserNotExists() {
        when(userServiceClient.getUser(anyLong())).thenThrow(FeignException.FeignClientException.class);

        assertFalse(userService.isUserExists(anyLong()));
    }
}