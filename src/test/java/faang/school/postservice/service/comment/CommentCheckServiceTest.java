package faang.school.postservice.service.comment;

import faang.school.postservice.config.comment.ModerationDictionary;
import faang.school.postservice.model.Comment;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentCheckServiceTest {
    @Mock
    private ModerationDictionary dictionary;
    @InjectMocks
    private CommentCheckService commentCheckService;

    @Test
    void checkComments() {
        int commentCount = 100000;
        int dictionarySize = 1000;
        List<Comment> comments = IntStream.range(0, commentCount)
                .boxed()
                .map(i -> Comment.builder()
                        .id(Long.valueOf(i))
                        .content("слово%d".formatted(i))
                        .build())
                .toList();
        List<String> dictionaryList = IntStream.range(0, dictionarySize)
                .boxed()
                .map("слово1%d"::formatted)
                .toList();

        when(dictionary.getDictionary()).thenReturn(dictionaryList);

        LocalDateTime now = LocalDateTime.now();
        List<Comment> actualList = commentCheckService.checkComments(comments).join();
        long validCount = actualList.stream()
                        .filter(Comment::getVerified)
                                .count();
        long unValidCount = actualList.stream()
                .filter(comment1 -> !comment1.getVerified())
                .count();

        assertEquals(commentCount, actualList.size());
        assertEquals(commentCount, validCount + unValidCount);
        assertTrue(validCount > 0);
        assertTrue(unValidCount > 0);
        assertTrue(actualList.stream()
                .allMatch(comment -> comment.getVerifiedDate().isAfter(now)));

        verify(dictionary, times(commentCount)).getDictionary();
    }
}