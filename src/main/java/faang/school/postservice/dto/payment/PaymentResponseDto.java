package faang.school.postservice.dto.payment;

import java.math.BigDecimal;

public record PaymentResponseDto(
        PaymentStatus status,
        int verificationCode,
        long paymentNumber,
        BigDecimal amount,
        Currency currency,
        String message
) {
}
