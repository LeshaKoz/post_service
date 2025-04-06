package faang.school.postservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class ThreadPoolConfig implements AsyncConfigurer {

    @Value("${task.execution.pool.core-size}")
    private int corePoolSize;

    @Value("${task.execution.pool.max-size}")
    private int maxPoolSize;

    @Value("${task.execution.pool.keep-alive}")
    private int keepAliveSeconds;
    @Value("${spring.data.redis.cache.thread-group-name}")
    private String threadGroupName;


    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setThreadGroupName(threadGroupName);
        executor.setKeepAliveSeconds(keepAliveSeconds);
        executor.initialize();
        return executor;
    }
}
