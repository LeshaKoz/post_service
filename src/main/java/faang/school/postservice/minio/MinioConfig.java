package faang.school.postservice.minio;

import faang.school.postservice.exception.minio_exceptions.BucketNotFoundException;
import faang.school.postservice.exception.minio_exceptions.MinioRemovingFileException;
import faang.school.postservice.exception.minio_exceptions.MinioUploadingFileException;
import faang.school.postservice.messages.ExceptionMessages;
import faang.school.postservice.model.Resource;
import io.minio.BucketExistsArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.StatObjectArgs;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Slf4j
@Component
public class MinioConfig {
    @Value("${services.minio.endpoint}")
    private String endpoint;

    @Value("${services.minio.access-key}")
    private String accessKey;

    @Value("${services.minio.secret-key}")
    private String secretKey;

    @Value("${services.minio.bucket-name}")
    private String bucketName;

    private MinioClient minioClient;

    @PostConstruct
    public void init() {
        this.minioClient = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }

    public Resource uploadImage(InputStream inputStream, byte[] imageBytes, String key, String name, String type) {
        try {
            boolean found =
                    minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!found) {
                log.error(ExceptionMessages.MINIO_BUCKET_NOT_FOUND_EXCEPTION);
                throw new BucketNotFoundException(ExceptionMessages.MINIO_BUCKET_NOT_FOUND_EXCEPTION);
            }

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(key)
                            .stream(inputStream, imageBytes.length, -1)
                            .build());
            log.info("File was added");
        } catch (Exception e) {
            log.error(ExceptionMessages.MINIO_UPLOADING_FILE_EXCEPTION);
            throw new MinioUploadingFileException(ExceptionMessages.MINIO_UPLOADING_FILE_EXCEPTION);
        }

        return Resource.builder()
                .key(key)
                .size(imageBytes.length)
                .name(name)
                .type(type)
                .build();
    }

    public Resource uploadVideoOrAudio(MultipartFile file, String key) {
        try {
            InputStream inputStream = file.getInputStream();
            boolean found =
                    minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!found) {
                log.error(ExceptionMessages.MINIO_BUCKET_NOT_FOUND_EXCEPTION);
                throw new BucketNotFoundException(ExceptionMessages.MINIO_BUCKET_NOT_FOUND_EXCEPTION);
            }

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(key)
                            .stream(inputStream, file.getSize(), -1)
                            .build());
            log.info("File was added");
        } catch (Exception e) {
            log.error(ExceptionMessages.MINIO_UPLOADING_FILE_EXCEPTION);
            throw new MinioUploadingFileException(ExceptionMessages.MINIO_UPLOADING_FILE_EXCEPTION);
        }

        return Resource.builder()
                .key(key)
                .size(file.getSize())
                .name(file.getName())
                .type(file.getContentType())
                .build();
    }

    public void delete(String key) {
        if (!exists(key)) {
            log.error(ExceptionMessages.MINIO_FILE_NOT_FOUND_EXCEPTION);
            throw new BucketNotFoundException.MinioFileNotFoundException(ExceptionMessages.MINIO_FILE_NOT_FOUND_EXCEPTION);
        }

        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(key)
                            .build()
            );
        } catch (Exception e) {
            log.error(ExceptionMessages.MINIO_REMOVING_FILE_EXCEPTION);
            throw new MinioRemovingFileException(ExceptionMessages.MINIO_REMOVING_FILE_EXCEPTION);
        }
    }

    private boolean exists(String imageName) {
        try {
            minioClient.statObject(StatObjectArgs.builder()
                    .bucket(bucketName)
                    .object(imageName)
                    .build());
            return true;
        } catch (Exception e) {
            log.error("File not found");
            return false;
        }
    }
}
