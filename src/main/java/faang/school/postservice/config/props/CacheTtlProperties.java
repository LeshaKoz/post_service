package faang.school.postservice.config.props;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "cache.ttl")
@RequiredArgsConstructor
@Getter
public class CacheTtlProperties {
    private final Duration post;
    private final Duration author;
}
