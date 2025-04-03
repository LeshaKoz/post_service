package faang.school.postservice.config.post;

public final class PostServiceConstants {

    private PostServiceConstants() {
    }

    public static class ThreadPool {
        public static final int EXECUTOR_POOL_THREAD_NUMBER = 10;
    }

    public static class TimeOut {
        public static final int CORRECT_POSTS_FUTURES_TIMEOUT = 30;
        public static final int CHECK_SPELLING_TIMEOUT = 5;
    }

    public static class AwaitTermination {
        public static final int EXECUTOR_AWAIT_TERMINATION = 10;
    }
}
