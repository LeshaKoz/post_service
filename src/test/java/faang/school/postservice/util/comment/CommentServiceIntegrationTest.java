package faang.school.postservice.util.comment;

import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.comment.CommentService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.concurrent.CompletableFuture;

import static org.awaitility.Awaitility.await;

@Testcontainers
@SpringBootTest
public class CommentServiceIntegrationTest {

    @Container
    public static PostgreSQLContainer<?> POSTGRESQL_CONTAINER =
            new PostgreSQLContainer<>("postgres:13.6");
    @Autowired
    private CommentService commentService;
    @Autowired
    private CommentRepository commentRepository;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Sql(scripts = {"classpath:db\\check_comments_test_data.sql"})
    @Test
    public void checkCommentsTest() {
        int nonCheckedComments = commentRepository.findIdsByVerifiedDateIsNull().size();
        Assertions.assertEquals(5, nonCheckedComments);

        Runnable task = commentService::checkComments;
        CompletableFuture<Void> executionFuture = CompletableFuture.runAsync(task);
        await().until(executionFuture::isDone);

        nonCheckedComments = commentRepository.findIdsByVerifiedDateIsNull().size();
        Assertions.assertEquals(0, nonCheckedComments);

        long unValidComments = commentRepository.countCommentsByVerified(false);
        Assertions.assertEquals(1, unValidComments);

        long validComments = commentRepository.countCommentsByVerified(true);
        Assertions.assertEquals(4, validComments);
    }
}
