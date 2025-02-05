package faang.school.postservice.handler;

import faang.school.postservice.exception.ExternalErrorException;
import faang.school.postservice.exception.PostBadRequestException;
import faang.school.postservice.exception.PostNotFoundException;
import faang.school.postservice.exception.ServiceNotAvailableException;
import feign.Response;
import feign.codec.ErrorDecoder;

import static faang.school.postservice.controller.ControllerExceptionHandler.DEFAULT_SERVICE_NAME;

public class CustomErrorDecoder implements ErrorDecoder {

    public static final String USER_SERVICE_CLIENT = "UserServiceClient";
    public static final String USER_SERVICE_NAME = "user-service";
    public static final String PROJECT_SERVICE_NAME = "project-service";
    public static final String PROJECT_SERVICE_CLIENT = "ProjectServiceClient";

    @Override

    public Exception decode(String methodKey, Response response) {
        String serviceName = extractServiceName(methodKey);

        return switch (response.status()) {
            case 400 -> new PostBadRequestException(serviceName, "Bad request");
            case 404 -> new PostNotFoundException(serviceName, determineNotFoundMessage(methodKey));
            case 503 -> new ServiceNotAvailableException(serviceName, "Service is unavailable");
            case 500 -> new ExternalErrorException(serviceName, "Unexpected error");
            default -> new ExternalErrorException(serviceName, "Unknown error");
        };
    }

    private String extractServiceName(String methodKey) {
        if (methodKey.contains(USER_SERVICE_CLIENT)) {
            return USER_SERVICE_NAME;
        } else if (methodKey.contains(PROJECT_SERVICE_CLIENT)) {
            return PROJECT_SERVICE_NAME;
        }
        return DEFAULT_SERVICE_NAME;
    }

    private String determineNotFoundMessage(String methodKey) {
        if (methodKey.contains(USER_SERVICE_CLIENT)) {
            return "User not found";
        } else if (methodKey.contains(PROJECT_SERVICE_CLIENT)) {
            return "Project not found";
        }
        return "Resource not found";
    }
}