package faang.school.postservice.exception;

import lombok.Getter;

@Getter
public class PostBadRequestException extends RuntimeException {
    private final String serviceName;

    public PostBadRequestException(String serviceName, String message) {
        super(message);
        this.serviceName = serviceName;
    }
}