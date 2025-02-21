package faang.school.postservice.exception;

public class KafkaProduceException extends RuntimeException {

    public KafkaProduceException(String message) {
        super(message);
    }
}