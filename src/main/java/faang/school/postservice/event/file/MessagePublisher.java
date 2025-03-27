package faang.school.postservice.event.file;

public interface MessagePublisher<T> {
    void publish(T message);
}
