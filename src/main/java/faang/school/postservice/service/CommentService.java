package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.user.UserForNewsFeedDto;
import faang.school.postservice.mapper.UserMapper;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.message.event.CommentEvent;
import faang.school.postservice.message.event.UsersBanEvent;
import faang.school.postservice.message.producer.KafkaCommentProducer;
import faang.school.postservice.message.producer.UsersBanPublisher;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.cache.UserCache;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.moderation.sightengine.SightEngineReactiveClient;
import faang.school.postservice.service.redis.RedisUserService;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final PostService postService;
    private final UserServiceClient userServiceClient;
    private final UsersBanPublisher usersBanPublisher;
    private final SightEngineReactiveClient textAnalysisService;
    private final RedisUserService redisUserService;
    private final UserMapper userMapper;
    private final KafkaCommentProducer kafkaCommentProducer;

    @Transactional
    public CommentDto addComment(long postId, CommentDto commentDto) {
        log.info("Trying to add comment: {} to post: {}", commentDto, postId);
        validateUserExists(commentDto.authorId());

        Post post = postService.findPostById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post with id %s not found".formatted(postId)));
        Comment comment = commentMapper.toEntity(commentDto);

        postService.addCommentToPost(post, comment);
        comment.setPost(post);

        comment = commentRepository.save(comment);
        saveAuthorOfTheCommentToCache(comment);
        kafkaCommentProducer.publish(createCommentEvent(comment));

        return commentMapper.toDto(comment);
    }

    @Transactional
    public CommentDto updateComment(long commentId, String content) {
        log.info("Trying to update comment: {} with the following content: {}",
                commentId, content);
        Comment comment = getCommentById(commentId);
        comment.setContent(content);
        return commentMapper.toDto(comment);
    }

    @Transactional
    public List<CommentDto> getPostComments(long postId) {
        log.info("Trying to get comments of post: {}", postId);
        return commentMapper.toDto(commentRepository.findAllByPostIdSortedByDate(postId));
    }

    @Transactional
    public void deleteComment(long commentId) {
        log.info("Trying to delete comment: {commentId}");
        commentRepository.deleteById(commentId);
    }

    public void publishUsersToBanEvent() {
        log.info("Trying to get all available comments");
        List<Comment> comments = new ArrayList<>(commentRepository.findAll());

        log.debug("Trying to convert all available comments to list of user id's to ban");
        Map<Long, Long> authorsUnverifiedCommentsAmount = comments.stream()
                .filter(Comment::isNotVerified)
                .collect(Collectors.groupingBy(Comment::getAuthorId, Collectors.counting()));

        List<Long> userIdsToBan = authorsUnverifiedCommentsAmount.entrySet().stream()
                .filter(entry -> entry.getValue() > 5)
                .map(Map.Entry::getKey)
                .toList();

        usersBanPublisher.publish(new UsersBanEvent(userIdsToBan));
    }


    public void verifyComments() {
        List<Comment> notVerifiedComments = commentRepository.findByVerifiedIsNull();
        log.info("Starting moderation of {} comments", notVerifiedComments.size());

        Flux.fromIterable(notVerifiedComments)
                .parallel()
                .runOn(Schedulers.parallel())
                .flatMap(comment -> textAnalysisService.analyzeText(comment.getContent())
                        .publishOn(Schedulers.boundedElastic())
                        .doOnNext(response -> {
                            comment.setVerified(textAnalysisService.textAnalysisProcessing(response));
                            comment.setVerifiedDate(LocalDateTime.now());
                            commentRepository.save(comment);
                        })
                        .doOnError(e -> log.error("Error processing comment '{}'", comment, e))
                        .onErrorResume(e -> Mono.empty())
                ).sequential()
                .doOnComplete(() -> log.info("The comment moderation process has been completed"))
                .doOnError(e -> log.error("Error during overall comment processing: ", e))
                .subscribe();
    }

    public Comment getCommentById(long commentId) {
        log.debug("start searching comment by ID {}", commentId);
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment is not found"));
    }

    public boolean isCommentNotExist(long commentId) {
        log.debug("start searching for existence comment with id {}", commentId);
        return !commentRepository.existsById(commentId);
    }

    private void validateUserExists(long userId) {
        try {
            userServiceClient.getUser(userId);
        } catch (FeignException ex) {
            throw new EntityNotFoundException("User does not exist");
        }
    }

    private void saveAuthorOfTheCommentToCache(Comment comment) {
        log.debug("Saving author of the comment to cache");
        UserForNewsFeedDto user = userServiceClient.getUserForNewsFeed(comment.getAuthorId());

        UserCache userCache = userMapper.toUserCache(user);
        redisUserService.saveUserToCache(userCache);
    }

    private CommentEvent createCommentEvent(Comment comment) {
        return CommentEvent.builder()
                .id(comment.getId())
                .authorId(comment.getAuthorId())
                .postId(comment.getPost().getId())
                .content(comment.getContent())
                .build();
    }
}
