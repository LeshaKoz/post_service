package faang.school.postservice.service;

import com.redis.testcontainers.RedisContainer;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentReadDto;
import faang.school.postservice.dto.post.PostReadDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.cache.PostCache;
import faang.school.postservice.model.cache.UserCache;
import faang.school.postservice.repository.cache.RedisPostRepository;
import faang.school.postservice.repository.cache.RedisUserRepository;
import faang.school.postservice.service.cache.RedisCacheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;

@SpringBootTest
@Testcontainers
public class RedisCacheServiceTest {

    @Container
    private static final RedisContainer REDIS_CONTAINER =
            new RedisContainer(DockerImageName.parse("redis/redis-stack:latest"));

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        REDIS_CONTAINER.start();
        REDIS_CONTAINER.waitingFor(Wait.forListeningPort());
        registry.add("spring.data.redis.port", () -> REDIS_CONTAINER.getMappedPort(6379));
        registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);
    }

    @Mock
    private RedisUserRepository redisUserRepository;

    @Mock
    private RedisPostRepository redisPostRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private RedisCacheService redisCacheService;

    private Long authorId;
    private PostReadDto postReadDto;

    @BeforeEach
    public void setUp() {
        authorId = 1L;
        postReadDto = PostReadDto.builder()
                .id(1L)
                .content("Тест")
                .authorId(1L)
                .build();
    }

    @Test
    void testSaveAuthorComment() {
        CommentReadDto commentReadDto = new CommentReadDto(
                1L,
                "Тест",
                authorId,
                List.of(1L, 2L),
                1L,
                List.of(1L),
                LocalDateTime.of(2021, 7, 1, 12, 0, 0, 0)
        );
        when(userServiceClient.getUser(authorId))
                .thenReturn(UserDto.builder().id(1L).build());

        redisCacheService.saveAuthorComment(commentReadDto);
        verify(redisUserRepository, times(1)).save(any(UserCache.class));
    }

    @Test
    void testSavePost() {
        redisCacheService.savePost(postReadDto);

        verify(redisPostRepository, times(1)).save(any(PostCache.class));
    }

    @Test
    void testSaveAuthorPost() {
        when(userServiceClient.getUser(authorId))
                .thenReturn(UserDto.builder().id(1L).build());

        redisCacheService.saveAuthorPost(postReadDto);
        verify(redisUserRepository, times(1)).save(any(UserCache.class));
    }
}
