package faang.school.postservice.service.image;

import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayOutputStream;

public interface S3Service {

    void uploadFile(long fileSize, String contentType, String key, byte[] byteArray);

    ByteArrayOutputStream resizeImage(MultipartFile file, int targetSize);
}
