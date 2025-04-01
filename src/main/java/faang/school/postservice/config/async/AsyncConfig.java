package faang.school.postservice.config.async;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@RequiredArgsConstructor
public class AsyncConfig {

    @Value("${heat.threads-per-instance}")
    private int threadsPerInstance;

    @Bean
    public ExecutorService cacheFixedThreadPool() {
        return Executors.newFixedThreadPool(threadsPerInstance);
    }
}
