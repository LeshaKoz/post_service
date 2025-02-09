package faang.school.postservice.schedule;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class ThreadPoolConfig {
    @Bean
    public ExecutorService executorService() {
        int threadNum = 10;
        return Executors.newFixedThreadPool(threadNum);
    }
}
