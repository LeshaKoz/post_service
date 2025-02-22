package faang.school.postservice.client;

import faang.school.postservice.dto.payment.PaymentRequestDto;
import faang.school.postservice.dto.payment.PaymentResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment-service", url = "${payment-service.host}:${payment-service.port}")
public interface PaymentServiceClient {

    @PostMapping("/api/payment")
    ResponseEntity<PaymentResponseDto> sendPayment(@RequestBody PaymentRequestDto paymentRequest);
}
