package faang.school.postservice.service.comment;

import faang.school.postservice.model.Comment;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CommentCheckServiceTest {

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
        CommentCheckService commentCheckService = new CommentCheckService(dictionaryList);

        LocalDateTime now = LocalDateTime.now();
        List<Comment> actualList = commentCheckService.checkComments(comments);
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
    }
}