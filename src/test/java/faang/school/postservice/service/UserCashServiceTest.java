package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.repository.redis.RedisUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import java.time.Duration;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserCashServiceTest {

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private RedisUserRepository redisUserRepository;

    @InjectMocks
    private UserCashService userCashService;

    private final Duration testTtl = Duration.ofHours(24);

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(userCashService, "userTtl", testTtl);
    }

    @Test
    void testCacheUser_Success() {
        Long userId = 123L;
        UserDto userDto = new UserDto(userId, "John Doe", "avatar.jpg");
        when(userServiceClient.getUser(userId)).thenReturn(userDto);

        userCashService.cacheUser(userId);

        verify(redisUserRepository, times(1)).save(userDto, testTtl);
        verifyNoMoreInteractions(redisUserRepository);
    }

    @Test
    void testCacheUser_UserNotFound() {
        Long userId = 456L;
        when(userServiceClient.getUser(userId)).thenReturn(null);

        userCashService.cacheUser(userId);

        verify(redisUserRepository, never()).save(any(), any());
    }

    @Test
    void testCacheUser_TtlPassedCorrectly() {
        Long userId = 789L;
        UserDto userDto = new UserDto(userId, "Alice", "avatar.png");
        when(userServiceClient.getUser(userId)).thenReturn(userDto);

        userCashService.cacheUser(userId);

        verify(redisUserRepository).save(userDto, testTtl);
    }
}