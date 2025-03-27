package faang.school.postservice.service.moderate;

import faang.school.postservice.config.app.PostServiceConfiguration;
import faang.school.postservice.service.moderate.model.WrongWordResourceItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ModerationDictionaryTest {
    @InjectMocks
    private ModerationDictionary moderationDictionary;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        List<WrongWordResourceItem> badWords = List.of(
                new WrongWordResourceItem("badword"),
                new WrongWordResourceItem("offensive"),
                new WrongWordResourceItem("curse")
        );
        ReflectionTestUtils.setField(moderationDictionary, "wrongWordResourceItems", badWords);
    }

    @Test
    void testContainsBadWords_whenTextContainsBadWord_thenReturnsTrue() {
        assertTrue(moderationDictionary.containsBadWords("This is a badword in the text"));
        assertTrue(moderationDictionary.containsBadWords("Some offensive language"));
    }

    @Test
    void testContainsBadWords_whenTextDoesNotContainBadWord_thenReturnsFalse() {
        assertFalse(moderationDictionary.containsBadWords("This is a clean text"));
        assertFalse(moderationDictionary.containsBadWords("Nothing wrong here"));
    }
}