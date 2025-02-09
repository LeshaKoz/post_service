package faang.school.postservice.service.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.exception.UserUnauthorizedAccessException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

@Slf4j
@RequiredArgsConstructor
@Service
public class CommentService {
    private final UserServiceClient userServiceClient;
    private final CommentRepository commentRepository;
    private final CommentCheckService commentCheckService;
    private final PostService postService;

    @Value("${comment.check.size}")
    private Integer pageSize;

    @Transactional
    public Comment createComment(Comment comment, Long postId) {
        getUser(comment.getAuthorId());
        Post post = postService.getPostById(postId);

        if (post.getComments() == null) {
            post.setComments(new ArrayList<>());
        }

        post.getComments().add(comment);
        postService.savePost(post);
        comment.setPost(post);

        log.info("Comment #{} to post #{} successfully created", comment.getId(), post.getId());
        return commentRepository.save(comment);
    }

    @Transactional
    public Comment updateComment(Comment comment) {
        Comment savedComment = getComment(comment.getId());
        checkUserIsOwnerComment(comment.getAuthorId(), savedComment.getAuthorId());

        savedComment.setContent(comment.getContent());
        savedComment.setUpdatedAt(LocalDateTime.now());

        log.info("Comment #{} successfully updated", savedComment.getId());
        return savedComment;
    }

    @Transactional(readOnly = true)
    public List<Comment> getAllCommentsToPost(Long postId) {
        Post post = postService.getPostById(postId);
        return post.getComments().stream()
                .sorted(Comparator.comparing(Comment::getUpdatedAt).reversed())
                .toList();
    }

    @Transactional
    public void deleteComment(Long commentId) {
        if (getComment(commentId) != null) {
            commentRepository.deleteById(commentId);
            log.info("Comment #{} successfully deleted", commentId);
        }
    }

    public void checkComments() {
        if (pageSize == null || pageSize <= 0) {
            pageSize = 100;
        }

        int pageCount = (int) Math.ceil((double) commentRepository.countByVerifiedDateIsNull()
                / pageSize);

        List<CompletableFuture<List<Comment>>> commentsFuture = IntStream.range(0, pageCount).boxed()
                .map(i -> {
                    Pageable pageable = PageRequest.of(i, pageSize);
                    List<Comment> notCheckedComments = commentRepository.findAllByVerifiedDateIsNull(pageable).toList();
                    return commentCheckService.checkComments(notCheckedComments)
                            .thenApply(commentRepository::saveAll);
                })
                .toList();
        CompletableFuture.allOf(commentsFuture.toArray(new CompletableFuture[0])).join();
    }

    private void checkUserIsOwnerComment(Long savedId, Long receivedId) {
        if (!Objects.equals(savedId, receivedId)) {
            throw new UserUnauthorizedAccessException("User cannot change comments the other users");
        }
    }

    public Comment getComment(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(String.format("Comment #%d not found or deleted", id)));
    }

    private void getUser(Long authorId) {
        try {
            userServiceClient.getUser(authorId);
        } catch (Exception e) {
            throw new NoSuchElementException(String.format("User with ID#%d not found", authorId));
        }
    }
}