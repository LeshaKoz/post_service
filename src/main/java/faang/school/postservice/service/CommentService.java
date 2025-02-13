package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.producer.KafkaCommentProducer;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.rediscacherepository.CommentCacheRepository;
import faang.school.postservice.validator.CommentValidator;
import faang.school.postservice.validator.PostValidator;
import faang.school.postservice.validator.UserValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final CommentValidator commentValidator;
    private final PostValidator postValidator;
    private final UserValidator userValidator;
    private final KafkaCommentProducer kafkaCommentProducer;
    private final CommentMapper commentMapper;
    private final UserContext userContext;
    private final CommentCacheRepository commentCacheRepository;
    private final UserServiceClient userServiceClient;

    @Transactional
    public CommentDto createComment(CommentDto commentDto) {
        long userId = userContext.getUserId();
        userValidator.validateUserId(userId);
        postValidator.validatePostExist(commentDto.getPostId());
        Comment comment = commentMapper.toEntity(commentDto);
        Post post = postRepository.findById(commentDto.getPostId())
                .orElseThrow(() ->
                        new EntityNotFoundException(String.format(CommentValidator.POST_NOT_FOUND, commentDto.getPostId())));
        comment.setPost(post);
        Comment commentSaved = commentRepository.save(comment);

        kafkaCommentProducer.send(commentMapper.toCommentEvent(comment));

        commentCacheRepository.saveAuthor(commentSaved.getId(), userServiceClient.getUser(commentSaved.getAuthorId()));
        return commentMapper.toDto(commentSaved);
    }

    @Transactional
    public CommentDto updateComment(CommentDto commentDto) {
        long userId = userContext.getUserId();
        userValidator.validateUserId(userId);
        postValidator.validatePostExist(commentDto.getPostId());
        Comment commentDB = commentRepository.findById(commentDto.getId())
                .orElseThrow(() ->
                        new EntityNotFoundException(String.format(CommentValidator.COMMENT_NOT_FOUND, commentDto.getId())));
        commentMapper.update(commentDto, commentDB);
        Comment commentSaved = commentRepository.save(commentDB);
        return commentMapper.toDto(commentSaved);
    }

    @Transactional(readOnly = true)
    public List<CommentDto> getAllComments(long postId) {
        postValidator.validatePostExist(postId);
        return commentRepository.findAllByPostId(postId).stream()
                .sorted(Comparator.comparing(Comment::getCreatedAt))
                .map(commentMapper::toDto).toList();
    }

    @Transactional
    public void deleteComment(long commentId) {
        commentValidator.validateCommentExist(commentId);
        commentRepository.deleteById(commentId);
    }
}
