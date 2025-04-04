package faang.school.postservice.service;

import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final Executor commentModeratorExecutor;
    private final ModerationDictionaryImpl moderationDictionary;

    @Value("${spring.task.scheduling.comment.max_comments_per_size}")
    private int limit;

    @Override
    public void moderateComments() {
        log.info("Start moderating comments");
        List<Long> unverifiedCommentsIds = commentRepository.getUnverifiedCommentsIds();

        for (int i = 0; i < unverifiedCommentsIds.size(); i += limit) {
            List<Long> subList = unverifiedCommentsIds.subList(i, Math.min(unverifiedCommentsIds.size(), i + limit));
            List<CompletableFuture<Void>> futures = commentRepository.getUnverifiedComments(subList).stream()
                    .map(this::moderateComment)
                    .toList();

            futures.forEach(CompletableFuture::join);
        }

        log.info("Finished moderating comments");
    }

    private CompletableFuture<Void> moderateComment(Comment comment) {
        return CompletableFuture.runAsync(() -> {
            try {
                String content = comment.getContent();
                if (content == null || content.isBlank()) {
                    comment.setVerified(true);
                } else {
                    comment.setVerified(moderationDictionary.isTextAreCorrect(content.toLowerCase()));
                }
                comment.setVerifiedAt(LocalDateTime.now());
                commentRepository.save(comment);
                log.debug("Comment: {} moderated. Is verified: {}", comment.getId(), comment.getVerified());
            } catch (Exception e) {
                log.error("Error moderating comment: {}", comment.getId(), e);
            }

        }, commentModeratorExecutor);
    }
}
