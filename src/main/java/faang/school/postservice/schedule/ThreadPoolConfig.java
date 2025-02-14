package faang.school.postservice.schedule;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class ThreadPoolConfig {
    private static final int THREADS_NUM = 10;
    @Bean
    public ExecutorService executorService() {
        return Executors.newFixedThreadPool(THREADS_NUM);
    }
}
