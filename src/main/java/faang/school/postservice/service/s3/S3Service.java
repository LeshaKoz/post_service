package faang.school.postservice.service.s3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import faang.school.postservice.exception.IntegrationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 s3client;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    public String uploadFile(MultipartFile file, String folder) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());
        String key = String.format("%s/%d%s", folder, System.currentTimeMillis(), file.getOriginalFilename());
        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucketName,
                    key,
                    file.getInputStream(),
                    objectMetadata);
            s3client.putObject(putObjectRequest);
        } catch (Exception e) {
            log.error("Ошибка при загрузке файла в хранилище", e);
            throw new IntegrationException("Ошибка при загрузке файла в хранилище ", e);
        }
        return key;
    }

    public void deleteFile(String key) {
        try {
            s3client.deleteObject(new DeleteObjectRequest(bucketName, key));
            log.info("Файл с ключом {} успешно удалён", key);
        } catch (AmazonServiceException e) {
            log.error("Ошибка при удалении файла из хранилища: {}", e.getMessage(), e);
            throw new IntegrationException("Ошибка при удалении файла из хранилища", e);
        }
    }

    public InputStream downloadFile(String key) {
        try {
            S3Object s3object = s3client.getObject(bucketName, key);
            return s3object.getObjectContent();
        } catch (AmazonServiceException e) {
            if (e.getStatusCode() == 404) {
                throw new IntegrationException("Файл с ключом " + key + " не найден в хранилище", e);
            }
            log.error("Ошибка при загрузке файла из хранилища: {}", e.getMessage(), e);
            throw new IntegrationException("Ошибка при загрузке файла из хранилища", e);
        }
    }
}