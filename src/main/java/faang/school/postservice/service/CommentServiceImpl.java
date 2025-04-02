package faang.school.postservice.service;

import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
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
    private final ModerationDictionary moderationDictionary;

    @Value("${spring.task.scheduling.comment.max_comments_per_size}")
    private int limit;

    @Override
    public void moderateComments() {
        int maxPage = getMaxPage();
        log.info("Start moderating comments, max page: {}", maxPage);

        for (int i = 0; i <= getMaxPage(); i++) {
            log.info("Moderating page: {}", i);
            List<CompletableFuture<Void>> futures =
                    commentRepository.getUnverifiedComments(PageRequest.of(i, limit)).stream()
                            .map(this::moderateComment)
                            .toList();

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        }

        log.info("Finished moderating comments");
    }

    private CompletableFuture<Void> moderateComment(Comment comment) {
        return CompletableFuture.runAsync(() -> {
            comment.setVerified(!moderationDictionary.isTextAreCorrect(comment.getContent()));
            comment.setVerifiedAt(LocalDateTime.now());
            commentRepository.save(comment);
            log.info("Comment: {} moderated. Is verified: {}", comment.getId(), comment.getVerified());
        }, commentModeratorExecutor);
    }


    private int getMaxPage() {
        return commentRepository.getUnverifiedCommentsCount() / limit;
    }
}
