package faang.school.postservice.config.props;

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
    @Positive
    private Integer batchSize;
    @Positive
    private Integer pageSize;
}
