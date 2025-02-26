package faang.school.postservice.service.post;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PostImageServiceTest {

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private PostImageService postImageService;

    private final int maxWidth = 1080;
    private final int maxHeightHorizontal = 566;

    @BeforeEach
    void setUp() {
        postImageService = new PostImageService();
        postImageService.maxWidth = maxWidth;
        postImageService.maxHeightHorizontal = maxHeightHorizontal;
    }

    @Test
    void getResizedCover_shouldReturnResizedImage() throws IOException {
        byte[] imageData = createTestImage();

        when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream(imageData));
        when(multipartFile.getOriginalFilename()).thenReturn("test.jpg");
        when(multipartFile.getContentType()).thenReturn("image/jpeg");

        MultipartFile resizedFile = postImageService.getResizedCover(multipartFile);

        assertNotNull(resizedFile);
        assertEquals("test.jpg", resizedFile.getOriginalFilename());
        assertEquals("image/jpeg", resizedFile.getContentType());

        BufferedImage resizedImage = ImageIO.read(resizedFile.getInputStream());
        assertNotNull(resizedImage);
        assertTrue(resizedImage.getWidth() <= maxWidth);
    }

    @Test
    void getResizedCover_shouldHandleIOException() throws IOException {
        when(multipartFile.getInputStream()).thenThrow(new IOException("Input stream error"));

        assertThrows(RuntimeException.class, () -> postImageService.getResizedCover(multipartFile));
    }

    @Test
    void calculateNewHeight_shouldReturnCorrectHeightForLandscapeImage() {
        BufferedImage image = new BufferedImage(1920, 1080, BufferedImage.TYPE_INT_RGB);
        int newHeight = postImageService.calculateNewHeight(image);

        assertEquals((int) (maxWidth * ((double) image.getHeight() / image.getWidth())), newHeight);
    }

    @Test
    void calculateNewHeight_shouldReturnMaxHeightForPortraitImage() {
        BufferedImage image = new BufferedImage(800, 1200, BufferedImage.TYPE_INT_RGB);
        int newHeight = postImageService.calculateNewHeight(image);

        assertEquals(maxHeightHorizontal, newHeight);
    }

    private byte[] createTestImage() throws IOException {
        BufferedImage img = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(img, "jpg", baos);
        baos.flush();
        return baos.toByteArray();
    }
}

