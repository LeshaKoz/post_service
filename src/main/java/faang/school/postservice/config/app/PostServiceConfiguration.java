package faang.school.postservice.config.app;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class PostServiceConfiguration {

    @Value("${post-service.thread-pool-size}")
    private int poolSize;

    @Bean
    public ExecutorService threadPool() {
        return Executors.newFixedThreadPool(poolSize);
    }
}
