package faang.school.postservice.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@Schema
public class ErrorResponse {
    @Schema(
            description = "HTTP статус ошибки",
            example = "400"
    )
    private int status;
    @Schema(
            description = "Тип ошибки",
            example = "Bad Request"
    )
    private String error;
    @Schema(
            description = "Сообщение об ошибке",
            example = "Поле content не должно быть пустым"
    )
    private String message;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(
            description = "Время возникновения ошибки",
            example = "2021-09-01 12:00:00"
    )
    private LocalDateTime timestamp = LocalDateTime.now();

    public ErrorResponse(String message, String error, HttpStatus status) {
        this.message = message;
        this.error = error;
        this.status = status.value();
    }
}
