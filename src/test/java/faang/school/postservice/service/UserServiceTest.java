package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.EntityNotFoundException;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    public static final long ID = 1L;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private UserContext userContext;
    @InjectMocks
    private UserService userService;

    @Test
    public void testUserExist() {
        Mockito.when(userServiceClient.getUser(ID)).thenReturn(UserDto.builder()
                .id(ID)
                .username("Alex")
                .email("email")
                .build());

        assertDoesNotThrow(() -> userService.getUserDtoById(ID));
    }

    @Test
    public void testUserNotExist() {
        Mockito.when(userServiceClient.getUser(ID)).thenThrow(FeignException.NotFound.class);

        assertThrows(EntityNotFoundException.class, () -> userService.getUserDtoById(ID));
    }
}
