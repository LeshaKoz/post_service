package faang.school.postservice.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class ImageService {

    private final MinioService minioService;

    public String saveImage(MultipartFile file, int targetSize, String bucketName) {
        try {
            BufferedImage image = ImageIO.read(file.getInputStream());
            BufferedImage resizedImage = resizeImage(image, targetSize);

            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                ImageIO.write(resizedImage, "png", baos);
                return minioService.uploadFile(baos.toByteArray(), bucketName);
            }
        } catch (IOException e) {
            log.error("Failed to process image", e);
            throw new RuntimeException("Failed to process image");
        }
    }

    private BufferedImage resizeImage(BufferedImage image, int targetSize) {
        try {
            return Thumbnails.of(image)
                    .size(targetSize, targetSize)
                    .keepAspectRatio(true)
                    .asBufferedImage();
        } catch (IOException e) {
            log.error("Failed to resize image", e);
            throw new RuntimeException("Failed to resize image");
        }
    }
}
