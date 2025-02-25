package faang.school.postservice.config.thread.pool;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@Data
public class ThreadPoolConfig {

    @Value("${thread-pool.verification-content-pool.num-of-thread}")
    private int verificationPoolNumOfThreads;

    @Value("${thread-pool.clearing-old-posts.num-of-thread}")
    private int clearingFeedPoolNumOfThreads;

    @Value("${thread-pool.post-event-processing.num-of-thread}")
    private int postEventProcessingNumOfThreads;

    public static final String VERIFICATION_POOL_BEAN_NAME = "verificationContentPool";

    @Bean(name = VERIFICATION_POOL_BEAN_NAME)
    public ExecutorService getVerificationContentPool() {
        return Executors.newFixedThreadPool(verificationPoolNumOfThreads);
    }

    @Bean
    public ThreadPoolTaskExecutor cleanOldPostsPool() {
        ThreadPoolTaskExecutor threadPool = new ThreadPoolTaskExecutor();
        threadPool.setCorePoolSize(clearingFeedPoolNumOfThreads);
        threadPool.initialize();
        return threadPool;
    }

    @Bean
    public ThreadPoolTaskExecutor postEventProcessingPool() {
        ThreadPoolTaskExecutor threadPool = new ThreadPoolTaskExecutor();
        threadPool.setCorePoolSize(postEventProcessingNumOfThreads);
        threadPool.initialize();
        return threadPool;
    }
}