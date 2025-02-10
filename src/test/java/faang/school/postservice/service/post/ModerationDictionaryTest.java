package faang.school.postservice.service.post;

import faang.school.postservice.utils.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ModerationDictionaryTest {
    @Mock
    private FileUtils fileUtils;

    @InjectMocks
    private ModerationDictionary moderationDictionary;

    @BeforeEach
    void setUp() {
        when(fileUtils.getAllLines(anyString())).thenReturn(List.of("bad"));
        moderationDictionary.readWordsFromFile();
    }

    @Test
    void testIsAllowedWithGoodContent() {
        String content = "content";

        boolean actual = moderationDictionary.isAllowed(content);

        assertTrue(actual);
    }

    @Test
    void testIsAllowedWithBadContent() {
        String content = "Bad content";

        boolean actual = moderationDictionary.isAllowed(content);

        assertFalse(actual);
    }
}
