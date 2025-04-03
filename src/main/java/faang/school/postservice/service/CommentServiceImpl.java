package faang.school.postservice.service;

import faang.school.postservice.config.context.AsyncConfig;
import faang.school.postservice.dto.moderation.ItemModerationResultDto;
import faang.school.postservice.dto.moderation.ItemToVerifyDto;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment_;
import faang.school.postservice.repository.CommentRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.JpaSort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final AsyncConfig asyncConfig;
    private final ModerationService moderationService;
    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;

    @Value("${app.comments-processing-batch-size}")
    private Integer pageSize;

    @PostConstruct
    public void validateProperties() {
        if (pageSize == null || pageSize <= 0) {
            throw new IllegalStateException(
                    "Property app.comments-processing-batch-size must be specified and greater than 0");
        }
    }

    @Async("taskExecutor")
    public void moderateComments() {
        var totalCommentsToVerify = commentRepository.countAllByVerifiedDateIsNull();

        for (int i = 0; i < totalCommentsToVerify; i += pageSize) {
            var pageRequest = createPageRequest(i, pageSize);

            Supplier<Stream<ItemToVerifyDto>> pageItemsFetcher = () -> commentMapper.toItemToVerifyDtosStream(
                    commentRepository.findByVerifiedDateIsNull(pageRequest).stream());
            moderationService.moderateItems(pageItemsFetcher).thenAcceptAsync(
                    moderationResults -> {
                        var verifiedItems = moderationResults.stream().filter(
                                ItemModerationResultDto::isVerified);
                        CompletableFuture.runAsync(() -> updateComments(verifiedItems, true),
                                asyncConfig.getAsyncExecutor());

                        var unverifiedItems = moderationResults.stream().filter(dto -> !dto.isVerified());
                        CompletableFuture.runAsync(() -> updateComments(unverifiedItems, false),
                                asyncConfig.getAsyncExecutor());
                    },
                    asyncConfig.getAsyncExecutor());
        }
    }

    protected PageRequest createPageRequest(int page, int pageSize) {
        return PageRequest.of(page, pageSize, JpaSort.of(Sort.Direction.ASC, Comment_.id));
    }

    private void updateComments(Stream<ItemModerationResultDto> moderationResults, boolean verified) {
        var commentIds = moderationResults.map(dto -> dto.item().id()).toList();
        commentRepository.updateVerifiedStatusAndDateByIds(commentIds, LocalDateTime.now(), verified);
    }
}
