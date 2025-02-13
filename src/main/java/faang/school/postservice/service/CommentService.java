package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentResponseDto;
import faang.school.postservice.dto.comment.CreateCommentDto;
import faang.school.postservice.dto.comment.UpdateCommentDto;
import faang.school.postservice.event.CommentEvent;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.OutboxEvent;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.CommentEventPublisher;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.OutboxEventRepository;
import faang.school.postservice.utils.Helper;
import faang.school.postservice.validator.CommentValidator;
import faang.school.postservice.validator.PostValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentAuthorCacheService commentAuthorCacheService;
    private final PostService postService;
    private final CommentMapper commentMapper;
    private final PostValidator postValidator;
    private final CommentValidator commentValidator;
    private final UserServiceClient userServiceClient;
    private final Helper helper;
    private final OutboxEventRepository outboxEventRepository;
    private final String AGGREGATE_TYPE = "Post";

    @Transactional
    public CommentResponseDto createComment(long postId, CreateCommentDto dto) {
        postValidator.validatePostExistsById(postId);
        userServiceClient.getUser(dto.getAuthorId());

        Comment comment = commentMapper.toEntity(dto);
        comment.setPost(postService.getPostById(postId));
        commentRepository.save(comment);
        log.info("New comment: {} to post: {} has been created", comment.getId(), comment.getPost().getId());

        OutboxEvent outboxEvent = OutboxEvent.builder()
                .aggregateId(postId)
                .aggregateType(AGGREGATE_TYPE)
                .eventType(CommentEvent.class.getSimpleName())
                .payload(createAndSerializeCommentEvent(comment))
                .createdAt(LocalDateTime.now())
                .processed(false)
                .build();

        outboxEventRepository.save(outboxEvent);

        commentAuthorCacheService.cacheCommentAuthor(comment.getId(), comment.getAuthorId());

        return commentMapper.toDto(comment);
    }

    @Transactional
    public CommentResponseDto updateComment(long postId, UpdateCommentDto dto) {
        postValidator.validatePostExistsById(postId);
        commentValidator.validateCommentExistsById(dto.getId());
        userServiceClient.getUser(dto.getAuthorId());

        Comment comment = getCommentById(dto.getId());
        commentValidator.validateCommentAuthorId(comment, dto.getAuthorId());
        comment.setContent(dto.getContent());
        commentRepository.save(comment);
        log.info("Comment: {} to post: {} has been updated", comment.getId(), comment.getPost().getId());

        return commentMapper.toDto(comment);
    }

    public List<CommentResponseDto> getAllComments(long postId) {
        postValidator.validatePostExistsById(postId);
        Post post = postService.getPostById(postId);
        List<Comment> comments = post.getComments().stream()
                .sorted(Comparator.comparing(Comment::getUpdatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();
        log.info("All comments to the post: {} has been received", post.getId());

        return commentMapper.toListDto(comments);
    }

    public void deleteComment(long postId, long commentId) {
        postValidator.validatePostExistsById(postId);
        commentValidator.validateCommentExistsById(commentId);
        commentRepository.deleteById(commentId);
        log.info("Comment: {} from post: {} was deleted successfully", commentId, postId);
    }

    public Comment getCommentById(Long id) {
        return commentRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(String.format("Comment with id: %s doesn't exist", id)));
    }

    private String createAndSerializeCommentEvent(Comment comment) {
        return helper.serializeToJson(
                CommentEvent.builder()
                        .commentAuthorId(comment.getAuthorId())
                        .postAuthorId(comment.getPost().getAuthorId())
                        .postId(comment.getPost().getId())
                        .commentId(comment.getId())
                        .build()
        );
    }
}