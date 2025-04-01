package faang.school.postservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Value("${spring.data.redis.cache.post.core-pool-size}")
    private int cachePostCorePoolSize;

    @Value("${spring.data.redis.cache.post.max-pool-size}")
    private int cachePostMaxPoolSize;

    @Value("${spring.data.redis.cache.post.queue-capacity}")
    private int cachePostQueueCapacity;

    @Value("${spring.data.redis.cache.post.thread-name-prefix}")
    private String cachePostThreadNamePrefix;

    @Value("${cache.authors.corePoolSize}")
    private int corePoolSize;

    @Value("${cache.authors.maxPoolSize}")
    private int maxPoolSize;

    @Value("${cache.authors.queueCapacity}")
    private int queueCapacity;

    @Value("${cache.authors.threadNamePrefix}")
    private String threadNamePrefix;

    @Bean(name = "cachePostExecutor")
    public Executor cachePostExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(cachePostCorePoolSize);
        executor.setMaxPoolSize(cachePostMaxPoolSize);
        executor.setQueueCapacity(cachePostQueueCapacity);
        executor.setThreadNamePrefix(cachePostThreadNamePrefix);
        executor.initialize();
        return executor;
    }

    @Bean(name = "commentAuthorCacheExecutor")
    public Executor commentAuthorCacheExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.initialize();
        return executor;
    }
}
