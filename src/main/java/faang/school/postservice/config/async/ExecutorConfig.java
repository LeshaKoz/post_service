package faang.school.postservice.config.async;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExecutorConfig {
    @Value("${spring.scheduler.post.publisher.threads-count}")
    private int threadsCountForPostPublisher;

    @Value("${spring.scheduler.comment.moderator.treads-count}")
    private int threadsCountCommentModerator;

    @Bean(name = "executorCommentModerator")
    public ExecutorService executorCommentModerator() {
        return Executors.newFixedThreadPool(threadsCountCommentModerator);
    }

    @Bean(name = "executorPostPublisher")
    public ExecutorService executorPostPublisher() {
        return Executors.newFixedThreadPool(threadsCountForPostPublisher);
    }
}
