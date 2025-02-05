package faang.school.postservice.exception;

import lombok.Getter;

@Getter
public class ServiceNotAvailableException extends RuntimeException {
    private final String serviceName;

    public ServiceNotAvailableException(String serviceName, String message) {
        super(message);
        this.serviceName = serviceName;
    }
}