package faang.school.postservice.dto;

import org.springframework.http.HttpStatus;

public record ErrorResponse(
        String serviceName,
        HttpStatus status,
        String errorCode,
        String errorMessage
) {
}
