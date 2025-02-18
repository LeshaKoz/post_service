package faang.school.postservice.builder;

public interface MessageBuilder<T> {
    String build(T object);
}
