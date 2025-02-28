package faang.school.postservice.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import faang.school.postservice.model.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class S3ServiceTest {
    @Mock
    private AmazonS3 amazonS3;

    @InjectMocks
    private S3Service s3Service;
    
    @BeforeEach
    void setUp() throws IOException {
        ReflectionTestUtils.setField(s3Service, "maxWidth", 500);
        ReflectionTestUtils.setField(s3Service, "maxHeight", 500);
    }

    @Test
    void uploadFile() throws IOException {
        BufferedImage image = new BufferedImage(300, 500, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos);
        byte[] imageBytes = baos.toByteArray();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);

        MultipartFile mockFile = new MockMultipartFile(
                "file",
                "test-image.jpg",
                "image/jpeg",
                inputStream
        );
        long fileSize = mockFile.getSize();
        Resource resource = new Resource();
        resource.setKey("null/" + mockFile.getOriginalFilename());
        resource.setName(mockFile.getOriginalFilename());
        resource.setType(mockFile.getContentType());
        resource.setSize(fileSize);


        Resource resourceResult = s3Service.uploadFile(mockFile);
        assertEquals(resource, resourceResult);
    }

    @Test
    void testUpload_file_with_exception() {
        assertThrows(IOException.class, () -> {
            InputStream inputStream = new FileInputStream("C:\\Users\\Rhanan\\Pictures\\kitt.jpg");
            MultipartFile file = new MockMultipartFile("file.txt", "file.txt",
                    "text/plain", inputStream);
            s3Service.uploadFile(file);
            inputStream.close();
        });
    }

    @Test
    void testDeleteFileCallsDeleteObject() {
        s3Service.deleteFile("test_folder/file.txt");
        verify(amazonS3, times(1)).deleteObject(null, "test_folder/file.txt");
    }
}
