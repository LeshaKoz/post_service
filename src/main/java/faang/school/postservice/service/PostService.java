package faang.school.postservice.service;

import faang.school.postservice.api.PerspectiveAPI;
import faang.school.postservice.exception.ModerationException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@RequiredArgsConstructor
@Service
public class PostService {
    public static final String MODERATION_FAIL_EXCEPTION = "Moderation failed for post";
    private final PostRepository postRepository;
    private final PerspectiveAPI perspectiveAPI;
    private final ExecutorService moderationExecutor;

    @Value("${moderation.batch.size}")
    private int pageSize;

    @Async
    public void moderatePosts() {
        int pageNumber = 0;
        Page<Post> page;

        do {
            page = postRepository.findByVerifiedDateIsNull(
                    PageRequest.of(pageNumber, pageSize, Sort.by("id").ascending())
            );

            List<CompletableFuture<Void>> futures = partition(page.getContent(), pageSize).stream()
                    .map(batch -> CompletableFuture.runAsync(() -> moderateBatch(batch), moderationExecutor))
                    .toList();

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

            pageNumber++;
        } while (page.hasNext());
    }

    @Retryable(retryFor = ModerationException.class, backoff = @Backoff(delay = 1000, multiplier = 2))
    public void moderateBatch(List<Post> batch) {
        for (Post post : batch) {
            try {
                boolean isToxic = perspectiveAPI.isContentToxic(post.getContent());

                post.setVerified(!isToxic);
                post.setVerifiedDate(LocalDateTime.now());

                postRepository.save(post);
                log.info("Post {} moderated. Toxic: {}", post.getId(), isToxic);
            } catch (IOException e) {
                log.error(MODERATION_FAIL_EXCEPTION);
                throw new ModerationException(MODERATION_FAIL_EXCEPTION);
            }
        }
    }

    private <T> List<List<T>> partition(List<T> list, int size) {
        List<List<T>> partitions = new ArrayList<>();
        for (int i = 0; i < list.size(); i += size) {
            partitions.add(list.subList(i, Math.min(i + size, list.size())));
        }
        return partitions;
    }

    public void setPageSizeForTesting(int pageSize) {
        this.pageSize = pageSize;
    }
}
