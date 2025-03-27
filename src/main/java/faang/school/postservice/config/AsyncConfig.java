package faang.school.postservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfig {

    @Value("${spring.data.redis.cache.post.core-pool-size}")
    private int cachePostCorePoolSize;

    @Value("${spring.data.redis.cache.post.max-pool-size}")
    private int cachePostMaxPoolSize;

    @Value("${spring.data.redis.cache.post.queue-capacity}")
    private int cachePostQueueCapacity;

    @Bean(name = "cachePostExecutor")
    public Executor cachePostExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(cachePostCorePoolSize);
        executor.setMaxPoolSize(cachePostMaxPoolSize);
        executor.setQueueCapacity(cachePostQueueCapacity);
        executor.setThreadNamePrefix("cache-post-");
        executor.initialize();
        return executor;
    }
}
