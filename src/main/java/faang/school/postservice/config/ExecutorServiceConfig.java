package faang.school.postservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ExecutorServiceConfig {

    @Bean(destroyMethod = "shutdown")
    @ConfigurationProperties(prefix = "task-executor.file-upload")
    public ThreadPoolTaskExecutor executorService() {
        return new ThreadPoolTaskExecutor();
    }
}
