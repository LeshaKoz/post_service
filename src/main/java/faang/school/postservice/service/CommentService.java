package faang.school.postservice.service;

import faang.school.postservice.cache.CommentAuthorCacheService;
import faang.school.postservice.config.kafka.KafkaProducer;
import faang.school.postservice.dto.comment.CommentCreateEventDto;
import faang.school.postservice.dto.comment.CommentResponse;
import faang.school.postservice.dto.comment.CommentUpdateRequest;
import faang.school.postservice.dto.comment.CreateCommentRequest;
import faang.school.postservice.exceptions.FileIsEmptyException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.utils.ImageService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

import static faang.school.postservice.config.MinioBuckets.COMMENT_IMAGE_BUCKET_NAME;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private static final int SMALL_IMAGE_SIZE = 170;
    private static final int LARGE_IMAGE_SIZE = 1080;
    private final ValidateService validateService;
    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;
    private final ImageService imageService;
    private final KafkaService kafkaService;
    private final PostService postService;
    private final KafkaProducer kafkaProducer;
    private final CommentAuthorCacheService commentAuthorCacheService;

    @Transactional
    public CommentResponse create(@Valid CreateCommentRequest createCommentRequest) {
        validateService.validateUser(createCommentRequest.userId());
        validateService.validatePost(createCommentRequest.postId());

        Post post = postService.getPostById(createCommentRequest.postId());
        Comment comment = commentMapper.toEntity(createCommentRequest);
        CommentCreateEventDto eventDto = CommentCreateEventDto.builder()
                .authorId(comment.getAuthorId())
                .postId(post.getId())
                .content(comment.getContent())
                .date(LocalDateTime.now())
                .build();
        kafkaService.sendCommentCreateMessage(eventDto);
        Comment savedComment = commentRepository.save(comment);
        try {
            retryableCacheCommentAuthor(savedComment.getAuthorId());
        } catch (Exception e) {
            log.error("Failed to cache comment author after retries", e);
        }
        return commentMapper.toCommentResponse(savedComment);
    }

    @Async
    @Retryable(
            value = {RedisConnectionFailureException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public void retryableCacheCommentAuthor(Long authorId) {
        try {
            commentAuthorCacheService.cacheCommentAuthor(authorId);
        } catch (RedisConnectionFailureException e) {
            log.warn("Attempt to cache comment author failed, retrying");
            throw e;
        }
    }

    @Transactional
    public CommentResponse update(@Valid CommentUpdateRequest updateRequest) {
        Comment comment = getComment(updateRequest.id());
        comment.setContent(updateRequest.content());
        return commentMapper.toCommentResponse(comment);
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getAllByPostId(@Valid @NotNull @Positive Long postId) {
        List<Comment> comments = commentRepository.findAllByPostId(postId);

        return comments.stream()
                .sorted((c1, c2) -> c2.getCreatedAt().compareTo(c1.getCreatedAt()))
                .map(commentMapper::toCommentResponse)
                .toList();
    }

    @Transactional
    public void delete(@Valid @NotNull @Positive Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new EntityNotFoundException("Comment with id " + commentId + " not found");
        }
        commentRepository.deleteById(commentId);
    }

    public boolean existsById(long commentId) {
        return commentRepository.existsById(commentId);
    }


    @Transactional
    public void uploadImage(@Valid @Positive Long commentId, MultipartFile file) {
        Comment comment = getComment(commentId);

        if (file.isEmpty()) {
            throw new FileIsEmptyException("");
        }

        String smallImageFileId = imageService.saveImage(file, SMALL_IMAGE_SIZE, COMMENT_IMAGE_BUCKET_NAME);
        String largeImageFileId = imageService.saveImage(file, LARGE_IMAGE_SIZE, COMMENT_IMAGE_BUCKET_NAME);

        comment.setLargeImageFileKey(largeImageFileId);
        comment.setSmallImageFileKey(smallImageFileId);
    }

    private Comment getComment(Long commentId) {
        if (commentId == null) {
            throw new IllegalArgumentException("commentId cannot be null");
        }

        return commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment with id " + commentId + " not found"));
    }

    public void processUnverifiedComments() {
        List<Long> authorIds = commentRepository.findAuthorIdsWithMoreThanFiveUnverifiedComments();
        authorIds.forEach(kafkaProducer::sendEventToUserServiceForBan);

        log.info("User ban events sent successfully.");
    }

}
