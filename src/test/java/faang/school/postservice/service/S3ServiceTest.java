package faang.school.postservice.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import faang.school.postservice.exception.IntegrationException;
import faang.school.postservice.service.s3.S3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class S3ServiceTest {

    private static final Long FILE_SIZE = 10L;

    @Mock
    private AmazonS3 s3client;

    @InjectMocks
    private S3Service s3service;

    @Value("${services.s3.bucketName}")
    private String bucketName = "corpbucket";

    private String key;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(s3service, "bucketName", "corpbucket");
        key = "file-key";
    }

    @Test
    void testUploadFile() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getSize()).thenReturn(FILE_SIZE);
        when(file.getContentType()).thenReturn("image/png");
        when(file.getOriginalFilename()).thenReturn("file.png");
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[0]));

        String folder = "uploads";
        String key = s3service.uploadFile(file, folder);

        verify(s3client).putObject(any(PutObjectRequest.class));
        assertTrue(key.startsWith(folder));
    }

    @Test
    void testDeleteFile_Exists() {
        s3service.deleteFile(key);

        verify(s3client).deleteObject(any(DeleteObjectRequest.class));
    }

    @Test
    void testDownloadFile() {
        S3Object s3Object = mock(S3Object.class);
        S3ObjectInputStream objectContent = new S3ObjectInputStream(new ByteArrayInputStream(new byte[0]), null);

        when(s3client.getObject(bucketName, key)).thenReturn(s3Object);
        when(s3Object.getObjectContent()).thenReturn(objectContent);

        InputStream result = s3service.downloadFile(key);

        verify(s3client).getObject(bucketName, key);
        assertNotNull(result);
    }

    @Test
    void testUploadFile_ThrowsException() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getSize()).thenReturn(FILE_SIZE);
        when(file.getContentType()).thenReturn("image/png");
        when(file.getOriginalFilename()).thenReturn("file.png");
        when(file.getInputStream()).thenThrow(new IOException("Ошибка при чтении файла"));

        assertThrows(IntegrationException.class, () -> s3service.uploadFile(file, "uploads"));
    }

    @Test
    void testDeleteFile_ThrowsException() {
        doThrow(new AmazonServiceException("Ошибка удаления"))
                .when(s3client).deleteObject(any(DeleteObjectRequest.class));

        assertThrows(IntegrationException.class, () -> s3service.deleteFile(key));
    }

    @Test
    void testDownloadFile_ReturnKey() {
        S3Object s3Object = mock(S3Object.class);
        S3ObjectInputStream objectContent = new S3ObjectInputStream(new ByteArrayInputStream(new byte[10]), null);

        when(s3client.getObject(bucketName, key)).thenReturn(s3Object);
        when(s3Object.getObjectContent()).thenReturn(objectContent);

        InputStream result = s3service.downloadFile(key);

        assertNotNull(result);
    }
}