package faang.school.postservice.config.threadpool;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Configuration
public class ThreadPoolConfig {

    @Value("${post-service.publish.thread-amount}")
    private int threadAmount;

    @Bean(destroyMethod = "shutdown", name = "publishExecutor")
    public ExecutorService executorService() {
        return Executors.newFixedThreadPool(threadAmount);
    }

    @Bean
    public ExecutorService postCreatedExecutorService(PostCreatedConfigProps props){
        return new ThreadPoolExecutor(
                props.corePoolSize,
                props.maxPoolSize,
                60,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(props.queueCapacity),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }
}