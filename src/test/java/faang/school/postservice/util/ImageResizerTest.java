package faang.school.postservice.util;

import faang.school.postservice.utilities.ImageResizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class ImageResizerTest {

    private static final int ORIGINAL_WIDTH = 100;
    private static final int ORIGINAL_HEIGHT = 100;
    private static final int RESIZED_WIDTH = 50;
    private static final int RESIZED_HEIGHT = 50;
    private static final String IMAGE_NAME = "image.jpg";
    private static final String IMAGE_CONTENT_TYPE = "image/jpeg";
    private static final String INVALID_FILE_NAME = "invalid.txt";
    private static final String INVALID_CONTENT_TYPE = "text/plain";
    private static final String INVALID_DATA = "invalid data";
    private static final String IMAGE_FORMAT = "jpg";
    private static final String RESIZED_FILE_PREFIX = "resized";
    private static final String RESIZED_FILE_SUFFIX = ".jpg";

    private ImageResizer imageResizer;
    private MultipartFile multipartFile;

    @BeforeEach
    void setUp() throws IOException {
        imageResizer = new ImageResizer();
        BufferedImage originalImage = new BufferedImage(ORIGINAL_WIDTH, ORIGINAL_HEIGHT, BufferedImage.TYPE_INT_RGB);
        byte[] originalImageBytes = imageToByteArray(originalImage);
        multipartFile = new MockMultipartFile(IMAGE_NAME, IMAGE_NAME, IMAGE_CONTENT_TYPE, originalImageBytes);
    }

    @Test
    void testResizeImage_resizeImageSuccessfully() throws IOException {
        MultipartFile resizedImage = imageResizer.resizeImage(multipartFile, RESIZED_WIDTH, RESIZED_HEIGHT);

        assertNotNull(resizedImage);
        assertFalse(resizedImage.isEmpty());
        assertEquals(multipartFile.getName(), resizedImage.getName());
        assertEquals(multipartFile.getOriginalFilename(), resizedImage.getOriginalFilename());
        assertEquals(multipartFile.getContentType(), resizedImage.getContentType());

        BufferedImage resizedBufferedImage = ImageIO.read(resizedImage.getInputStream());
        assertNotNull(resizedBufferedImage);
        assertEquals(RESIZED_WIDTH, resizedBufferedImage.getWidth());
        assertEquals(RESIZED_HEIGHT, resizedBufferedImage.getHeight());
    }

    @Test
    void testResizeImage_invalidImageFormat() {
        MultipartFile invalidMultipartFile = new MockMultipartFile(INVALID_FILE_NAME, INVALID_FILE_NAME, INVALID_CONTENT_TYPE, INVALID_DATA.getBytes());

        assertThrows(IllegalArgumentException.class, () -> imageResizer.resizeImage(invalidMultipartFile, RESIZED_WIDTH, RESIZED_HEIGHT));
    }

    @Test
    void testResizedImage_transferToFile() throws IOException {

        MultipartFile resizedImage = imageResizer.resizeImage(multipartFile, RESIZED_WIDTH, RESIZED_HEIGHT);

        File tempFile = File.createTempFile(RESIZED_FILE_PREFIX, RESIZED_FILE_SUFFIX);
        tempFile.deleteOnExit();

        resizedImage.transferTo(tempFile);

        assertTrue(tempFile.exists());
        assertTrue(tempFile.length() > 0);

        BufferedImage readFromFile = ImageIO.read(tempFile);
        assertNotNull(readFromFile);
        assertEquals(RESIZED_WIDTH, readFromFile.getWidth());
        assertEquals(RESIZED_HEIGHT, readFromFile.getHeight());
    }

    private byte[] imageToByteArray(BufferedImage image) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(image, IMAGE_FORMAT, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
}