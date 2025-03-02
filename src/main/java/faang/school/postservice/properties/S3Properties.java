package faang.school.postservice.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "services.s3")
public class S3Properties {
    private long maxImageSizeMb;
    private int maxImagesCountForPost;
    private ImageProperties image;

    @Data
    public static class ImageProperties {
        private int maxWidthHorizontal;
        private int maxHeightHorizontal;
        private int maxSideSquare;
    }
}