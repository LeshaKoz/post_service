package faang.school.postservice.service.image;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface S3Service {

    void uploadFile(long fileSize, String contentType, String key, byte[] byteArray);

    void uploadFile(MultipartFile file, String key);

    InputStream downloadFile(String key);
}
