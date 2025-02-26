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
    private static final int SCHEDULED_CORE_POOL_SIZE = 1;
    private static final int SCHEDULE_MAXIMUM_POOL_SIZE = 1;
    private static final long KEEP_ALIVE_TIME = 0L;
    private static final int COMMENT_CORE_POOL_SIZE = 1;
    private static final int COMMENT_MAXIMUM_POOL_SIZE = 1;


    @Bean(name = "scheduledCommentExecutorService")
    public ExecutorService scheduledCommentExecutorService() {
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(1);

        return new ThreadPoolExecutor(SCHEDULED_CORE_POOL_SIZE, SCHEDULE_MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS, workQueue,
                new ThreadPoolExecutor.AbortPolicy());
    }

    @Bean(name = "commentExecutorService")
    public ExecutorService commentExecutorService() {
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(30);

        return new ThreadPoolExecutor(COMMENT_CORE_POOL_SIZE, COMMENT_MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME,
                TimeUnit.SECONDS, workQueue, new ThreadPoolExecutor.DiscardPolicy());
    }
}
