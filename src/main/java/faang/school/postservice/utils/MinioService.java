package faang.school.postservice.utils;

import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class MinioService {

    private final MinioClient minioClient;

    public String uploadFile(byte[] byteArray, String bucketName) {
        try {
            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
        } catch (Exception e) {
            log.error("Minio create bucket error", e);
            throw new RuntimeException("Minio create bucket error");
        }

        String fileId = UUID.randomUUID().toString();
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(byteArray)) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileId + ".png")
                            .stream(inputStream, byteArray.length, -1)
                            .contentType("image/png")
                            .build()
            );
        } catch (Exception e) {
            log.error("Minio put object error", e);
            throw new RuntimeException("Minio put object error");
        }

        return fileId;
    }

    public byte[] getFile(String bucketName, String fileId) {
        try (InputStream inputStream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileId + ".svg")
                        .build()
        )) {
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }

            return result.toByteArray();
        } catch (Exception e) {
            log.error("Minio get object error", e);
            throw new RuntimeException("Minio get object error");
        }
    }
}
