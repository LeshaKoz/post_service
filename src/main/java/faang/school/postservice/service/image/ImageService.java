package faang.school.postservice.service.image;

import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;

public interface ImageService {
    void resizeAndUploadImage(String key, Boolean isSmall, MultipartFile file);

    ByteArrayOutputStream resizeImage(MultipartFile file, int targetSize);
}
