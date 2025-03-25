package faang.school.postservice.service.comment.implementations;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.exception.AuthorNotFoundException;
import faang.school.postservice.exception.CommentNotFoundException;
import faang.school.postservice.exception.PostIdMismatchException;
import faang.school.postservice.exception.PostNotFoundException;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.comment.interfaces.CommentService;
import faang.school.postservice.service.post.interfaces.PostService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final PostService postService;
    private final UserServiceClient userServiceClient;
    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;

    @Override
    public CommentDto createComment(Long postId, CommentDto commentDto) {
        if (!postId.equals(commentDto.getPostId())) {
            log.error("Post ID mismatch: path={}, dto={}", postId, commentDto.getPostId());
            throw new PostIdMismatchException("Post ID in path and DTO must match");
        }

        Post post = postService.getPostById(postId);

        checkAuthor(commentDto.getAuthorId());

        Comment comment = commentMapper.toEntity(commentDto);
        comment.setPost(post);
        Comment savedComment = commentRepository.save(comment);
        return commentMapper.toDto(savedComment);
    }

    @Override
    public CommentDto updateComment(Long postId, Long commentId, CommentDto commentDto) {
        Comment comment = checkComment(postId, commentId);

        comment.setContent(commentDto.getContent());
        Comment savedComment = commentRepository.save(comment);
        return commentMapper.toDto(savedComment);
    }

    @Override
    public List<CommentDto> getCommentsByPostId(Long postId) {
        List<Comment> comments = commentRepository.findAllByPostId(postId);

        return comments.stream()
                .sorted(Comparator.comparing(Comment::getCreatedAt).reversed())
                .map(commentMapper::toDto)
                .toList();
    }

    @Override
    public void deleteComment(Long postId, Long commentId) {
        Comment comment = checkComment(postId, commentId);

        commentRepository.delete(comment);
    }

    private Comment checkComment(Long postId, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Comment with id " + commentId + " not found"));

        checkAuthor(comment.getAuthorId());

        if (comment.getPost() == null) {
            log.error("Post not found for comment {}", commentId);
            throw new PostNotFoundException("Post for comment with id " + commentId + " not found");
        }
        if (!postId.equals(comment.getPost().getId())) {
            log.error("Comment {} is not associated with post {}", commentId, postId);
            throw new PostIdMismatchException("Comment is not associated with post " + postId);
        }

        return comment;
    }

    private void checkAuthor(Long authorId) {
        try {
            userServiceClient.getUser(authorId);
        } catch (FeignException e) {
            log.error("Author not found: id={}", authorId, e);
            throw new AuthorNotFoundException("Author with id " + authorId + " not found");
        }
    }
}
