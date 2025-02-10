package faang.school.postservice.dto.payment;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PaymentRequest(
        long paymentNumber,

        @Min(1)
        @NotNull(message = "Сумма платежа не должны быть null")
        BigDecimal amount,

        @NotNull(message = "Тип валюты не должен быть null")
        Currency currency
) {
}
