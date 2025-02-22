package faang.school.postservice.service.post;

import faang.school.postservice.config.props.PostProperties;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.GrammarService;
import faang.school.postservice.service.PaginationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostProcessingService {
    private final PostRepository postRepository;
    private final PostProperties postProperties;
    private final GrammarService grammarService;
    private final ModerationDictionary moderationDictionary;
    private final PaginationService paginationService;

    public void moderatePosts() {
        log.info("Начало модерации постов");
        concurrencyProcessPosts(
                postRepository::findAllNotVerified,
                this::moderatePostsBatch,
                postProperties.getModeration().getPageSize(),
                postProperties.getModeration().getBatchSize()
        );
        log.info("Конец модерации постов");
    }

    public void checkGrammar() {
        log.info("Начало проверки орфографии постов");
        concurrencyProcessPosts(
                postRepository::findAllNotPublishedAndVerifiedTrue,
                this::checkGrammarBatch,
                postProperties.getGrammar().getPageSize(),
                postProperties.getGrammar().getBatchSize()
        );
        log.info("Конец проверки орфографии постов");
    }

    private Stream<Post> checkGrammarBatch(List<Post> posts) {
        return posts.parallelStream()
                .peek(post -> post.setContent(
                        grammarService.correctText(post.getContent())
                ));
    }

    private Stream<Post> moderatePostsBatch(List<Post> posts) {
        LocalDateTime verifiedDate = LocalDateTime.now();
        return posts.parallelStream()
                .filter(post -> moderationDictionary.isAllowed(post.getContent()))
                .peek(post -> {
                    post.setVerified(true);
                    post.setVerifiedDate(verifiedDate);
                });
    }

    private void concurrencyProcessPosts(
            Function<Pageable, Page<Post>> getPostsFunction,
            Function<List<Post>, Stream<Post>> processFunction,
            int pageSize,
            int batchSize
    ) {
        Pageable pageable = PageRequest.of(0, pageSize);
        Page<Post> page;
        int pageNumber = 0;

        do {
            page = getPostsFunction.apply(pageable);
            List<Post> postsToSave = paginationService.processInParallel(
                    page.getContent(),
                    batchSize,
                    processFunction
            );
            postRepository.saveAll(postsToSave);
            pageNumber++;
            pageable = PageRequest.of(pageNumber, pageSize);
        } while (!page.isLast());
    }
}
