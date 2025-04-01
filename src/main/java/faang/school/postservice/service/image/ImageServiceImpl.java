package faang.school.postservice.service.image;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {
    private final S3Service s3Service;
    @Value("${image.smallImageSize}")
    private int smallImageSize;
    @Value("${image.largeImageSize}")
    private int largeImageSize;

    @Override
    public void resizeAndUploadImage(String key, Boolean isSmall, MultipartFile file) {
        String contentType = file.getContentType();
        ByteArrayOutputStream byteArrayOutputStream;
        if (isSmall) {
            byteArrayOutputStream = resizeImage(file, smallImageSize);
        } else {
            byteArrayOutputStream = resizeImage(file, largeImageSize);
        }
        s3Service.uploadFile(byteArrayOutputStream.size(), contentType, key, byteArrayOutputStream.toByteArray());
    }

    public ByteArrayOutputStream resizeImage(MultipartFile file, int targetSize) {
        try (ByteArrayOutputStream thumbnailOutputStream = new ByteArrayOutputStream()) {
            Thumbnails.of(file.getInputStream())
                    .size(targetSize, targetSize)
                    .keepAspectRatio(true)
                    .toOutputStream(thumbnailOutputStream);
            return thumbnailOutputStream;
        } catch (IOException e) {
            throw new RuntimeException("Failed to resize image", e);
        }
    }
}
