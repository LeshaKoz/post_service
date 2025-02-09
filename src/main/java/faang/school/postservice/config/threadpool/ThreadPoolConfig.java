package faang.school.postservice.config.threadpool;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RequiredArgsConstructor
@Configuration
public class ThreadPoolConfig {

    @Value("${post-service.publish.thread-amount}")
    private int threadAmount;

    @Bean(destroyMethod = "shutdown", name = "publishExecutor")
    public ExecutorService executorService() {
        return Executors.newFixedThreadPool(threadAmount);
    }
}