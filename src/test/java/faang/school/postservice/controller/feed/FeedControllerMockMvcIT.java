package faang.school.postservice.controller.feed;

import com.redis.testcontainers.RedisContainer;
import faang.school.postservice.service.feed.FeedService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@ActiveProfiles({"test", "dev"})
class FeedControllerMockMvcIT {

  private static final String URL_BASE = "/api/v1/feed";

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private FeedService feedService;

  @Container
  public static final PostgreSQLContainer<?> POSTGRE_SQL_CONTAINER = new PostgreSQLContainer<>(
      "postgres:13:3");

  @Container
  public static final RedisContainer REDIS_CONTAINER = new RedisContainer(
      DockerImageName.parse("redis/redis-stack:latest"));

  @Container
  public static final KafkaContainer KAFKA_CONTAINER = new KafkaContainer(
      DockerImageName.parse("confluentinc/cp-kafka:7.0.0"));

  @DynamicPropertySource
  static void start(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", POSTGRE_SQL_CONTAINER::getJdbcUrl);
    registry.add("spring.datasource.username", POSTGRE_SQL_CONTAINER::getUsername);
    registry.add("spring.datasource.password", POSTGRE_SQL_CONTAINER::getPassword);
    registry.add("spring.liquibase.contexts", () -> "test");

    registry.add("spring.data.redis.port", () -> REDIS_CONTAINER.getMappedPort(6379));
    registry.add("spring.data.redis", REDIS_CONTAINER::getHost);

    registry.add("bootstrap-servers", KAFKA_CONTAINER::getBootstrapServers);

    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  @DisplayName("test")
  void testGetUserFeed() {

  }

}