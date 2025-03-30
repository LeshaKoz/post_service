package faang.school.postservice.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class RedisConfigTest {
    @Autowired(required = false)
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    void redisTemplateConfigured() {
        assertNotNull(redisTemplate, "Redis template should be configured");
    }
}
