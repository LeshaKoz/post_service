package faang.school.postservice.config.props;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "post")
@Component
@Data
@Validated
public class PostProperties {
    @Valid
    private final Moderation moderation = new Moderation();

    @Valid
    private final Grammar grammar = new Grammar();

    @Valid
    private final Schedule schedule = new Schedule();

    @Data
    public static class Moderation {
        @Positive
        @NotNull
        private Integer batchSize;
        @Positive
        @NotNull
        private Integer pageSize;
    }

    @Data
    public static class Grammar {
        @Positive
        @NotNull
        private Integer batchSize;
        @Positive
        @NotNull
        private Integer pageSize;
    }

    @Data
    public static class Schedule {
        @Positive
        @NotNull
        private Integer batchSize;
        @Positive
        @NotNull
        private Integer pageSize;
    }
}
