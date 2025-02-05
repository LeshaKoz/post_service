package faang.school.postservice.exception;

import lombok.Getter;

@Getter
public class PostNotFoundException extends RuntimeException {
    private final String serviceName;

    public PostNotFoundException(String serviceName, String message) {
        super(message);
        this.serviceName = serviceName;
    }
}
