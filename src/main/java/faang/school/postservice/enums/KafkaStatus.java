package faang.school.postservice.enums;

import lombok.Getter;

@Getter
public enum KafkaStatus {
    PENDING("PENDING"),
    FAILED("FAILED");

    private final String value;

    KafkaStatus(String value) {
        this.value = value;
    }
}