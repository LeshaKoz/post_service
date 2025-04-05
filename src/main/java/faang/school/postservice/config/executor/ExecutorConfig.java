package faang.school.postservice.config.executor;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ExecutorService;

@Configuration
public class ExecutorConfig {

    @Value("${app.thread-pool.core-size}")
    private int coreSize;

    @Value("${app.thread-pool.max-size}")
    private int maxSize;

    @Value("${app.thread-pool.queue-capacity}")
    private int queueCapacity;

    @Bean(name = "postServiceTaskExecutor")
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(coreSize);
        executor.setMaxPoolSize(maxSize);
        executor.setQueueCapacity(queueCapacity);
        executor.initialize();
        return executor;
    }

    @Bean
    public ExecutorService executorService(@Qualifier("postServiceTaskExecutor") ThreadPoolTaskExecutor taskExecutor) {
        return taskExecutor.getThreadPoolExecutor();
    }
}
