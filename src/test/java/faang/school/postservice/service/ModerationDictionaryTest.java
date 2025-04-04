package faang.school.postservice.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = ModerationDictionary.class)
@TestPropertySource(locations = "classpath:application-test.yaml")
public class ModerationDictionaryTest {
    @Autowired
    private ModerationDictionary moderationDictionary;

    @Test
    public void isTextAreCorrectTest() {
        String correctText = "This is a text";
        String incorrectText = "This is a test";

        Assertions.assertTrue(moderationDictionary.isTextAreCorrect(correctText));
        Assertions.assertFalse(moderationDictionary.isTextAreCorrect(incorrectText));
    }
}
