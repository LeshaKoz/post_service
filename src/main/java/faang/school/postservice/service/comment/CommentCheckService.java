package faang.school.postservice.service.comment;

import faang.school.postservice.config.comment.ModerationDictionary;
import faang.school.postservice.model.Comment;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Validated
@Slf4j
@RequiredArgsConstructor
@Service
public class CommentCheckService {

    private final ModerationDictionary moderationDictionary;

    @Async("commentExecutorService")
    public CompletableFuture<List<Comment>> checkComments(@NotNull List<Comment> comments) {
        log.info("Checking comments in Thread {}", Thread.currentThread().getName());
        List<Comment> resultComments = comments.stream()
                .map(this::checkComment)
                .toList();
        return CompletableFuture.completedFuture(resultComments);
    }

    private Comment checkComment(Comment comment) {
        boolean isValid = moderationDictionary.getDictionary().stream()
                .noneMatch(word -> comment.getContent().toLowerCase().contains(word.toLowerCase()));
        comment.setVerified(isValid);
        comment.setVerifiedDate(LocalDateTime.now());
        return comment;
    }
}
