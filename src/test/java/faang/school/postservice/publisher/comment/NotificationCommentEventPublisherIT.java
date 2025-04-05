package faang.school.postservice.publisher.comment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.kafka.KafkaTestConfig;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.event.NotificationCommentEvent;
import faang.school.postservice.service.CommentService;
import faang.school.postservice.service.PostService;
import faang.school.postservice.service.ScheduledPostPublisher;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.Collections;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ActiveProfiles("test")
@ContextConfiguration(classes = KafkaTestConfig.class)
@Testcontainers
@SpringBootTest(
        properties = {
                "spring.redis.host=localhost",
                "spring.redis.port=6379",
                "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration"
        }
)
public class NotificationCommentEventPublisherIT {

    @Value("${spring.kafka.topics.notification-comment-topic.name}")
    private String notificationCommentTopicName;

    @Autowired
    private NotificationCommentEventPublisher commentPublisher;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private Consumer<String, String> consumer;

    @MockBean
    private PostService postService;

    @MockBean
    private CommentService commentService;

    private Post post;
    private Comment comment;

    @BeforeEach
    public void setup() {
        post = Post.builder()
                .id(1L).build();
        comment = Comment.builder()
                .id(1L)
                .post(post)
                .content("test-comment")
                .build();
    }

    @Container
    static final KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.3.0"));

    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withUsername("user")
            .withPassword("password");

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.url", postgres::getJdbcUrl);

        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }

    @Test
    public void testCommentNotificationEventIsSent() throws JsonProcessingException {
        consumer.subscribe(Collections.singletonList(notificationCommentTopicName));

        commentPublisher.publishEvent(comment);

        ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(5));
        assertThat(records.count()).isGreaterThan(0);

        for (ConsumerRecord<String, String> record : records) {
            NotificationCommentEvent event = objectMapper.readValue(record.value(), NotificationCommentEvent.class);
            assertThat(event.getCommentId()).isEqualTo(comment.getId());
        }
    }

}
