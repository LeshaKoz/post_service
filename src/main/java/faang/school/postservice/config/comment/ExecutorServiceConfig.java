package faang.school.postservice.config.comment;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
public class ExecutorServiceConfig {
    @Bean(name = "scheduledCommentExecutorService")
    public ExecutorService scheduledCommentExecutorService() {
        int corePoolSize = 1;
        int maximumPoolSize = 1;
        long keepAliveTime = 0L;
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(1);

        return new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue,
                new ThreadPoolExecutor.DiscardPolicy());
    }

    @Bean(name = "commentExecutorService")
    public ExecutorService commentExecutorService() {
        int corePoolSize = 5;
        int maximumPoolSize = 10;
        long keepAliveTime = 0;
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(30);

        return new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue,
                new ThreadPoolExecutor.DiscardPolicy());
    }
}
