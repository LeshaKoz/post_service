package faang.school.postservice.service.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;
    private final CommentMapper commentMapper;

    @Transactional
    public CommentDto createComment(@Valid CommentDto commentDto) {
        log.info("Creating a comment for post ID: {} by user ID: {}", commentDto.getPostId(), commentDto.getAuthorId());

        if (!postRepository.existsById(commentDto.getPostId())) {
            throw new EntityNotFoundException("Post with ID " + commentDto.getPostId() + " does not exist.");
        }

        try {
            UserDto userDto = userServiceClient.getUser(commentDto.getAuthorId());
            if (userDto == null) {
                throw new EntityNotFoundException("User not found with ID: " + commentDto.getAuthorId());
            }

            Comment comment = commentMapper.toEntity(commentDto);
            Comment savedComment = commentRepository.save(comment);
            log.info("Comment created with ID: {}", savedComment.getId());
            return commentMapper.toDto(savedComment);

        } catch (FeignException e) {
            log.error("Error while fetching user from userService: {}", e.getMessage());
            throw new EntityNotFoundException("Error while verifying user existence: " + e.getMessage());
        }
    }

    public CommentDto updateComment(long commentId, CommentDto commentDto) {
        log.info("Updating comment with ID: {}", commentId);

        Comment existingComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalStateException("Comment not found with ID: " + commentId));

        if (!existingComment.getAuthorId().equals(commentDto.getAuthorId())) {
            throw new IllegalArgumentException("Only the author of the comment can update it.");
        }

        if (commentDto.getContent() != null && !commentDto.getContent().isBlank()) {
            existingComment.setContent(commentDto.getContent());
            existingComment.setUpdatedAt(LocalDateTime.now());
        } else {
            throw new IllegalArgumentException("Content cannot be blank.");
        }

        Comment updatedComment = commentRepository.save(existingComment);
        log.info("Comment with ID: {} updated successfully.", commentId);

        return commentMapper.toDto(updatedComment);
    }

    public List<CommentDto> getAllCommentsByPostId(long postId) {
        log.info("Fetching comments for post ID: {} in chronological order", postId);
        List<Comment> comments = commentRepository.findAllByPostId(postId);
        return comments.stream()
                .map(commentMapper::toDto)
                .sorted(Comparator.comparing(CommentDto::getCreatedAt).reversed())
                .toList();
    }

    @Transactional
    public void deleteComment(long commentId) {
        log.info("Deleting comment with ID: {}", commentId);
        if (!commentRepository.existsById(commentId)) {
            throw new EntityNotFoundException("Comment not found with ID: " + commentId);
        }

                commentRepository.deleteById(commentId);
        log.info("Comment with ID: {} has been deleted.", commentId);
    }
}