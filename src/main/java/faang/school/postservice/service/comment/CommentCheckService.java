package faang.school.postservice.service.comment;

import faang.school.postservice.model.Comment;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;

@Validated
@Slf4j
@Service
public class CommentCheckService {

    private final List<String> badWords;

    public CommentCheckService(@Qualifier("badWords") List<String> badWords) {
        this.badWords = badWords;
    }

    public List<Comment> checkComments(@NotNull List<Comment> comments) {
        log.info("Checking comments in Thread {}", Thread.currentThread().getName());
        return comments.stream()
                .map(this::checkComment)
                .toList();
    }

    private Comment checkComment(Comment comment) {
        boolean isValid = badWords.stream()
                .noneMatch(word -> comment.getContent().toLowerCase().contains(word.toLowerCase()));
        comment.setVerified(isValid);
        comment.setVerifiedDate(LocalDateTime.now());
        return comment;
    }
}
