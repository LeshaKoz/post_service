package faang.school.postservice.exception;

public class KafkaMessageSendingException extends RuntimeException {

    public KafkaMessageSendingException(String message, Throwable cause) {
        super(message, cause);
    }
}
