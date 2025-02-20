package faang.school.postservice.service.post;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.CommentDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.comment.AccessDeniedCommentException;
import faang.school.postservice.mapper.post.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.post.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.post.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final CommentMapper commentMapper;
    private final UserServiceClient userServiceClient;

    @Override
    public CommentDto createComment(CommentDto dto) {
        UserDto author = userServiceClient.getUser(dto.authorId());
        if (author == null) {
            log.error("Author with id = {} not found", dto.authorId());
            throw new EntityNotFoundException(String.format("Author with id = %d not found", dto.authorId()));
        }
        log.info("Get user with id = {} from user_service", author.id());
        return commentMapper.toDto(commentRepository.save(buildComment(dto)));
    }

    @Override
    public CommentDto updateComment(CommentDto dto) {
        Comment comment = commentRepository.findById(dto.id()).orElseThrow(() -> new EntityNotFoundException(
                String.format("Comment with id = %d not found", dto.id())
        ));
        log.info("Found comment with id = {}", comment.getId());
        if (comment.getAuthorId() != dto.authorId()) {
            log.error("Access Access denied to comment for user id = {}", dto.authorId());
            throw new AccessDeniedCommentException(String.format(
                    "Access denied to comment for user id = %d", dto.authorId()));
        }
        comment.setContent(dto.content());
        return commentMapper.toDto(commentRepository.save(comment));
    }

    @Override
    public List<CommentDto> findAllCommentsByPostId(long postId) {
        return commentRepository.findAllByPostId(postId).stream()
                .sorted(Comparator.comparing(Comment::getCreatedAt))
                .map(commentMapper::toDto)
                .toList();
    }

    @Override
    public void deleteCommentById(long id) {
        commentRepository.deleteById(id);
        log.info("Deleted comment with id = {}", id);
    }

    private Comment buildComment(CommentDto dto) {
        Post post = postRepository.findById(dto.authorId()).orElseThrow(() ->
                new EntityNotFoundException(String.format("Post with id = %d not found", dto.postId())));
        log.info("Found post with id = {}", post.getId());
        return Comment.builder()
                .authorId(dto.authorId())
                .post(post)
                .createdAt(LocalDateTime.now())
                .content(dto.content())
                .build();
    }
}
