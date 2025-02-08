package faang.school.postservice.service.image;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import faang.school.postservice.config.AwsProperties;
import faang.school.postservice.exception.UploadFileException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Slf4j
@Service
@AllArgsConstructor
public class S3ServiceImpl implements S3Service {
    private final AwsProperties awsProperties;
    private final AmazonS3 s3Client;

    @Override
    public void uploadFile(long fileSize, String contentType, String key, byte[] byteArray) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(fileSize);
        objectMetadata.setContentType(contentType);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(byteArray);

        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    awsProperties.getBucketName(), key, inputStream, objectMetadata);
            s3Client.putObject(putObjectRequest);
        } catch (Exception e) {
            throw new UploadFileException("Error uploading file:" + e);
        }
    }

    @Override
    public ByteArrayOutputStream resizeImage(MultipartFile file, int targetSize) {
        ByteArrayOutputStream thumbnailOutputStream = new ByteArrayOutputStream();
        try {
            Thumbnails.of(file.getInputStream())
                    .size(targetSize, targetSize)
                    .keepAspectRatio(true)
                    .toOutputStream(thumbnailOutputStream);
        } catch (IOException e) {
            log.error("Failed to resize image", e);
            throw new UploadFileException("Failed to resize image");
        }
       return thumbnailOutputStream;
    }
}
