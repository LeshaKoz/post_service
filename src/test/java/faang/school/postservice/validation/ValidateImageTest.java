package faang.school.postservice.validation;

import faang.school.postservice.exception.DataValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты валидации изображений")
public class ValidateImageTest {

    private ValidateImage validateImage = new ValidateImage();

    @Nested
    @DisplayName("Проверка валидности изображения")
    class ValidateImageFile {

        @Test
        @DisplayName("Валидное изображение")
        void givenValidImageFile_whenValidate_thenSuccess() {
            MockMultipartFile file = new MockMultipartFile(
                    "file", "test.jpg", "image/jpeg", new byte[1024]);

            assertDoesNotThrow(() -> validateImage.validateImageFile(file));
        }

        @Test
        @DisplayName("Пустой файл")
        void givenEmptyFile_whenValidate_thenThrowException() {
            MockMultipartFile file = new MockMultipartFile(
                    "file", "empty.jpg", "image/jpeg", new byte[0]);

            assertThrows(DataValidationException.class,
                    () -> validateImage.validateImageFile(file));
        }

        @Test
        @DisplayName("Слишком большой файл")
        void givenLargeFile_whenValidate_thenThrowException() {
            MockMultipartFile file = new MockMultipartFile(
                    "file", "large.jpg", "image/jpeg", new byte[5 * 1024 * 1024 + 1]);

            assertThrows(DataValidationException.class,
                    () -> validateImage.validateImageFile(file));
        }

        @Test
        @DisplayName("Неизображение")
        void givenNonImageFile_whenValidate_thenThrowException() {
            MockMultipartFile file = new MockMultipartFile(
                    "file", "text.txt", "text/plain", "text".getBytes());

            assertThrows(DataValidationException.class,
                    () -> validateImage.validateImageFile(file));
        }
    }
}