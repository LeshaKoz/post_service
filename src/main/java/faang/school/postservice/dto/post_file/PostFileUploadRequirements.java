package faang.school.postservice.dto.post_file;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "file-upload.post")
public class PostFileUploadRequirements {
    private int minAmount;
    private int maxAmount;
    private Map<String, FileTypoInfo> infoByFileType;
    private ImageSizeLimits imageSizeLimits;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class FileTypoInfo {
        private int maxSize;
        private Set<String> allowedExtensions;
    }

    @Getter
    @Setter
    public static class ImageSizeLimits {
        private Square square;
        private Rectangular rectangular;

        @Getter
        @Setter
        public static class Square {
            private int maxLength;
        }

        @Getter
        @Setter
        public static class Rectangular {
            private int maxLongSideLength;
            private int maxShortSideLength;
        }
    }
}
