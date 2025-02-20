package faang.school.postservice.dto.payment;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PaymentRequest(
        @NotNull(message = "Payment number can't be null")
        @Min(value = 1, message = "Minimum payment number is 1")
        long paymentNumber,

        @NotNull(message = "Amount can't be null")
        @Min(value = 1, message = "Minimum amount is 1")
        BigDecimal amount,

        @NotNull(message = "Currency can't be null")
        Currency currency
) {
}
