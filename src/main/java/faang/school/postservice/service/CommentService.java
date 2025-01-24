package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CreateCommentRequest;
import faang.school.postservice.dto.comment.CreateCommentResponse;
import faang.school.postservice.dto.comment.UpdateCommentRequest;
import faang.school.postservice.dto.comment.UpdatedCommentResponse;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final PostService postService;
    private final UserServiceClient userServiceClient;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    @Transactional
    public CreateCommentResponse createComment(CreateCommentRequest createCommentRequest) {
        Post post = verificationCreatingData(createCommentRequest.getAuthorId(),
                createCommentRequest.getPostId());

        Comment entity = commentMapper.toEntity(createCommentRequest);
        entity.setPost(post);
        commentRepository.save(entity);
        return commentMapper.toResponse(entity);
    }


    @Transactional
    public UpdatedCommentResponse updateComment(UpdateCommentRequest updateCommentRequest) {
        Comment comment = validateForUpdate(updateCommentRequest.getId(),
                updateCommentRequest.getAuthorId(),
                updateCommentRequest.getContent());

        commentMapper.updateComment(comment, updateCommentRequest);
        commentRepository.save(comment);
        return commentMapper.toUpdatedComment(comment);
    }


    @Transactional(readOnly = true)
    public List<Comment> getListComment(Long postId) {
        return commentRepository.findAllByPostId(postId).stream()
                .sorted(Comparator.comparing(Comment::getCreatedAt).reversed())
                .toList();
    }

    @Transactional
    public void deleteComment(Long commentId) {
        Comment comment = getComment(commentId);
        commentRepository.deleteById(comment.getId());
    }

    public Comment getComment(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with id: " + commentId));
    }

    private Post verificationCreatingData(Long authorId, Long postId) {
        if (userServiceClient.getUser(authorId) == null) {
            throw new EntityNotFoundException(String.format("User with id: %s not found",
                    authorId));
        }
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));
        return post;
    }

    private Comment validateForUpdate(Long commentId, Long authorId, String content) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));
        if (!comment.getAuthorId().equals(authorId)) {
            throw new IllegalArgumentException("You are not the author of this comment.");
        }
        if (comment.getContent().equals(content)) {
            throw new IllegalArgumentException("The comment has not been changed.");
        }
        return comment;
    }
}