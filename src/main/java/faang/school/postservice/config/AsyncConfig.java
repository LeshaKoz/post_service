package faang.school.postservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@EnableAsync
@Configuration
public class AsyncConfig {

    @Bean(name = "commonTaskExecutor")
    public ThreadPoolTaskExecutor moderationExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("moderation-");
        executor.initialize();
        return executor;
    }

    @Value("${app.async.hash_generator.core_pool_size:5}")
    private int corePoolSize;

    @Value("${app.async.hash_generator.max_pool_size:10}")
    private int maxPoolSize;

    @Value("${app.async.hash_generator.queue_capacity:100}")
    private int queueCapacity;

    @Value("${app.async.hash_generator.thread_name_prefix:hash-}")
    private String threadNamePrefix;

    @Bean(name = "hashGeneratorExecutor")
    public Executor hashGeneratorExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.initialize();
        return executor;
    }
}