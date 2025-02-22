package faang.school.postservice.exception;

public class RedisTransactionException extends RuntimeException{
    public RedisTransactionException(String message) {
        super(message);
    }
}
