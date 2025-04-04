package faang.school.postservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.listener.ChannelTopic;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RedisServiceImplTest {
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private SetOperations<String, Object> setOperations;
    @Mock
    private ChannelTopic channelTopic;
    @InjectMocks
    private RedisServiceImpl redisService;

    @Test
    public void pushToRedisUsersForBanTest() {
        when(redisTemplate.opsForSet()).thenReturn(setOperations);
        when(setOperations.add(channelTopic.getTopic(), 1L)).thenReturn(22L);

        redisService.pushToRedisUsersForBan(1L);

        verify(setOperations, times(1)).add(channelTopic.getTopic(), 1L);
        verify(redisTemplate, times(1)).opsForSet();
    }
}
