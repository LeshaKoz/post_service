package faang.school.postservice.util.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redis.testcontainers.RedisContainer;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.RequestPostDto;
import faang.school.postservice.dto.user.UserDto;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import java.util.List;
import java.util.Properties;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
public class PostControllerTest {
    public static final List<NewTopic> topicList = List.of(
            new NewTopic("user-like-post", 3, (short) 1),
            new NewTopic("user-post-viewed", 3, (short) 1)
    );
    private static final DockerImageName REDIS_IMAGE = DockerImageName.parse("redis:7.4.2");
    @Container
    public static final RedisContainer REDIS_CONTAINER = new RedisContainer(REDIS_IMAGE);
    private static final DockerImageName KAFKA_IMAGE = DockerImageName.parse("confluentinc/cp-kafka:latest");
    @Container
    public static final KafkaContainer KAFKA_CONTAINER = new KafkaContainer(KAFKA_IMAGE);
    @Container
    public static PostgreSQLContainer<?> POSTGRESQL_CONTAINER =
            new PostgreSQLContainer<>("postgres:13.6");
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserServiceClient userServiceClient;

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        POSTGRESQL_CONTAINER.start();
        REDIS_CONTAINER.start();
        KAFKA_CONTAINER.start();
        createTopic();

        registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);

        registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);
        registry.add("spring.data.redis.port", REDIS_CONTAINER::getFirstMappedPort);
        registry.add("spring.data.redis.post-ttl-key", () -> "post-id-ttl");
        registry.add("spring.data.redis.post-id-key", () -> "post-id");
        registry.add("spring.data.redis.post-ttl-hours", () -> 1);

        registry.add("spring.kafka.bootstrap.server.address", KAFKA_CONTAINER::getBootstrapServers);
        registry.add("spring.kafka.topics.analytic-topics.add-like-topic-name",
                () -> "user-like-post");
        registry.add("spring.kafka.topics.analytic-topics.post-view-topic",
                () -> "user-post-viewed");

    }

    private static void createTopic() {
        Properties properties = new Properties();
        properties.put(
                AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_CONTAINER.getBootstrapServers()
        );
        try (Admin admin = Admin.create(properties)) {
            admin.createTopics(topicList);
        }
    }

    @BeforeEach
    public void setup() {
        when(userServiceClient.getUser(anyLong()))
                .thenReturn(new UserDto(1L, "user", "user@mail"));
    }


    @Test
    public void testCreatePostByUserId() throws Exception {
        Long userId = 1L;
        RequestPostDto requestPostDto = new RequestPostDto();
        requestPostDto.setContent("Content");

        mockMvc.perform(post("/post/create-by-user/{user-id}", userId)
                        .header("x-user-id", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("x-user-id", userId.toString())
                        .content(objectMapper.writeValueAsString(requestPostDto)))
                .andExpect(status().isCreated());
    }

    @Test
    public void testCreatePostByUserId_WithBlankContent() throws Exception {
        Long userId = 1L;
        RequestPostDto requestPostDto = new RequestPostDto();
        requestPostDto.setContent(" ");

        mockMvc.perform(post("/post/create-by-user/{user-id}", userId)
                        .header("x-user-id", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestPostDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreatePostByUserId_WithNullContent() throws Exception {
        Long userId = 1L;
        RequestPostDto requestPostDto = new RequestPostDto();
        requestPostDto.setContent(null);

        mockMvc.perform(post("/post/create-by-user/{user-id}", userId)
                        .header("x-user-id", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestPostDto)))
                .andExpect(status().isBadRequest());
    }

    @Sql(scripts = "/db/clear-post.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Sql(scripts = "/db/insert-post.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Test
    void testGetPostById() throws Exception {
        long userId = 2L;
        Long postId = 1L;
        mockMvc.perform(get("/post/post/{post-id}", postId)
                        .header("x-user-id", Long.toString(userId)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

    }
}