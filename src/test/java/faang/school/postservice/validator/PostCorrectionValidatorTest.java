package faang.school.postservice.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PostCorrectionValidatorTest {

    private PostCorrectionValidator validator;

    @BeforeEach
    void setUp() {
        validator = new PostCorrectionValidator();
    }

    @Test
    void isTextValid_ShouldReturnFalse_WhenTextIsNull() {
        assertFalse(validator.isTextValid(null));
    }

    @Test
    void isTextValid_ShouldReturnFalse_WhenTextIsBlank() {
        assertFalse(validator.isTextValid("   "));
    }

    @Test
    void isTextValid_ShouldReturnTrue_WhenTextIsNotBlank() {
        assertTrue(validator.isTextValid("Hello world"));
    }

    @Test
    void isCorrectionValid_ShouldReturnFalse_WhenTextIsNull() {
        assertFalse(validator.isCorrectionValid(null));
    }

    @Test
    void isCorrectionValid_ShouldReturnFalse_WhenTextIsBlank() {
        assertFalse(validator.isCorrectionValid("   "));
    }

    @Test
    void isCorrectionValid_ShouldReturnTrue_WhenTextIsNotBlank() {
        assertTrue(validator.isCorrectionValid("Corrected text"));
    }

    @Test
    void isCorrectionDifferent_ShouldReturnFalse_WhenTextIsSame() {
        assertFalse(validator.isCorrectionDifferent("Text", "Text"));
    }

    @Test
    void isCorrectionDifferent_ShouldReturnTrue_WhenTextIsDifferent() {
        assertTrue(validator.isCorrectionDifferent("Wrong text", "Corrected text"));
    }
}