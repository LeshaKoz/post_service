package faang.school.postservice.dto.error;

public record ErrorModel(String message, int statusCode, String serviceName) {
}
