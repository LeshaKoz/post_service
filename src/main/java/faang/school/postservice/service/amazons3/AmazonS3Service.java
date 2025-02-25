package faang.school.postservice.service.amazons3;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

public interface AmazonS3Service {
    void uploadFile(MultipartFile file, String folder) throws IOException;

    InputStream getFile(String fileId);

    void deleteFile(String key);
}
