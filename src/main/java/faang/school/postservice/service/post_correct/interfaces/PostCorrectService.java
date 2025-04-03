package faang.school.postservice.service.post_correct.interfaces;

import faang.school.postservice.model.Post;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public interface PostCorrectService {
    CompletableFuture<Void> correctPost(Post post, ExecutorService executor);

    CompletableFuture<String> checkSpellingWithRetry(String content);

    String parseCorrectedContent(String responseBody, String originalContent);
}
