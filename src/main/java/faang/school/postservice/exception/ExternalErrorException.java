package faang.school.postservice.exception;

import lombok.Getter;

@Getter
public class ExternalErrorException extends RuntimeException {
    private final String serviceName;

    public ExternalErrorException(String serviceName, String message) {
        super(message);
        this.serviceName = serviceName;
    }
}