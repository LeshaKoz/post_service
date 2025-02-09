package faang.school.postservice.service.comment;

import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommentModerator {

    private final CommentService commentService;
    private final CommentRepository commentRepository;

    @Value("${moderation.comment-limit}")
    private int commentLimit;
    @Value("${moderation.batch-size}")
    private int batchSize;

    @Scheduled(cron = "${moderation.cron}")
    public void runModeration() {
        List<Comment> comments;
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        do {
            comments = commentRepository.findUnverifiedWithLimit(commentLimit);
            if (!comments.isEmpty()) {
                List<List<Comment>> batches = partitionList(comments, batchSize);

                for (List<Comment> batch : batches) {
                    CompletableFuture<Void> future = commentService.moderateComments(batch);
                    futures.add(future);
                }
            }
        } while (!comments.isEmpty());

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        log.info("All comment is verified !");
    }

    public <T> List<List<T>> partitionList(List<T> list, int batchSize) {
        List<List<T>> partitions = new ArrayList<>();
        for (int i = 0; i < list.size(); i += batchSize) {
            partitions.add(list.subList(i, Math.min(i + batchSize, list.size())));
        }
        return partitions;
    }

}
