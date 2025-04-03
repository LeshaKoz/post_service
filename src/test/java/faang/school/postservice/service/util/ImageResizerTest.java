package faang.school.postservice.service.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты изменения размера изображений")
public class ImageResizerTest {

    private ImageResizer imageResizer = new ImageResizer();

    @Nested
    @DisplayName("Изменение размера изображения")
    class ResizeImage {

        @Test
        @DisplayName("Уменьшение альбомного изображения")
        void givenLandscapeImage_whenResize_thenMaintainAspectRatio() {
            BufferedImage original = new BufferedImage(2000, 1000, BufferedImage.TYPE_INT_RGB);

            BufferedImage resized = imageResizer.resize(original, 1000);

            assertEquals(1000, resized.getWidth());
            assertEquals(500, resized.getHeight());
        }

        @Test
        @DisplayName("Уменьшение портретного изображения")
        void givenPortraitImage_whenResize_thenMaintainAspectRatio() {
            BufferedImage original = new BufferedImage(800, 1200, BufferedImage.TYPE_INT_RGB);

            BufferedImage resized = imageResizer.resize(original, 600);

            assertEquals(400, resized.getWidth());
            assertEquals(600, resized.getHeight());
        }

        @Test
        @DisplayName("Уменьшение квадратного изображения")
        void givenSquareImage_whenResize_thenMaintainAspectRatio() {
            BufferedImage original = new BufferedImage(1500, 1500, BufferedImage.TYPE_INT_RGB);

            BufferedImage resized = imageResizer.resize(original, 500);

            assertEquals(500, resized.getWidth());
            assertEquals(500, resized.getHeight());
        }
    }
}