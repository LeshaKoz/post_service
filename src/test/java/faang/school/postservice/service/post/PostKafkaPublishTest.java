package faang.school.postservice.service.post;

import faang.school.postservice.PostServiceApp;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostCreatedEvent;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.FeedRepository;
import faang.school.postservice.util.BaseContextTest;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = PostServiceApp.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Testcontainers
class PostKafkaPublishTest extends BaseContextTest {
    @Value("${kafka.topic.post}")
    private String createPostTopic;

    @MockBean
    private UserServiceClient userServiceClient;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private FeedRepository feedRepository;

    @Autowired
    private PostService postService;

    private KafkaConsumer<String, Object> kafkaConsumer;

    @BeforeAll
    void beforeAll() {
        Properties props = getProperties();

        kafkaConsumer = new KafkaConsumer<>(props);
        kafkaConsumer.subscribe(List.of(createPostTopic));

        Awaitility.await().atMost(30, TimeUnit.SECONDS).until(() -> {
            kafkaConsumer.poll(Duration.ofMillis(100));
            return !kafkaConsumer.assignment().isEmpty();
        });
    }

    @Sql(scripts = {
            "/db/add_users_test_data.sql",
            "/db/add_subscribers_test_data.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/db/cleanup_test_data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void shouldPublishKafkaEventWhenPostIsPublished() {
        Post post = new Post();
        post.setAuthorId(1L);
        post.setContent("test content");
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());

        postRepository.save(post);

        postService.publishPost(post.getId());

        Awaitility.await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
            ConsumerRecords<String, Object> records = kafkaConsumer.poll(Duration.ofMillis(500));
            assertThat(records.count()).isPositive();

            ConsumerRecord<String, Object> record = records.iterator().next();
            assertThat(record.value()).isInstanceOf(PostCreatedEvent.class);

            PostCreatedEvent event = (PostCreatedEvent) record.value();
            assertThat(event.postId()).isEqualTo(post.getId());
            assertThat(event.authorId()).isEqualTo(1L);
            assertThat(event.subscriberIds()).containsExactly(2L, 3L, 4L);

            event.subscriberIds().forEach(id -> {
                Set<Object> feed = feedRepository.getFeed(id, 10);
                assertTrue(feed.contains(String.valueOf(post.getId())),
                        "Feed for subscriber " + id + " should contain post id " + post.getId());
            });
        });

        postRepository.delete(post);
    }

    private Properties getProperties() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_CONTAINER.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class.getName());
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "10");
        props.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, "1");
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "15000");
        props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, "5000");
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, "300000");
        return props;
    }
}
