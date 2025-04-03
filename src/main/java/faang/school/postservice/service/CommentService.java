package faang.school.postservice.service;

import faang.school.postservice.exception.NotFoundException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import jakarta.persistence.Column;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private static final String COMMENT_NOT_FOUND_PATTERN = "Comment with ID: %s not found";

    private final PostService postService;
    private final CommentRepository commentRepository;

    public Comment getComment(Long commentId) {
        return commentRepository.findById(commentId)
                        .orElseThrow(() -> new NotFoundException(
                                String.format(COMMENT_NOT_FOUND_PATTERN, commentId)));
    }

    public List<Comment> getComments(Long postId) {
        return commentRepository.findAllByPostId(postId);
    }

    public Comment createComment(Long postId, Comment comment) {
        Post post = postService.getPost(postId);
        comment.setPost(post);
        comment.setId(null);

        return commentRepository.save(comment);
    }

    public Comment updateComment(Long commentId, Comment updatesForComment) {
        Comment originalComment = getComment(commentId);
        if (updateCommentFromUpdates(originalComment, updatesForComment)) {
            commentRepository.save(originalComment);
        }
        return originalComment;
    }

    public boolean deleteComment(Long commentId) {
        Comment originalComment = getComment(commentId);
        if (null != originalComment) {
            commentRepository.delete(getComment(commentId));
            return true;
        }
        return false;
    }

    private boolean updateCommentFromUpdates(Comment original, Comment updates) {
        boolean commentWasUpdated = false;

        String newContent = updates.getContent();
        if (null != newContent && !newContent.equals(original.getContent())) {
            original.setContent(newContent);
            commentWasUpdated = true;
        }

        String newLargeImageFileKey = updates.getLargeImageFileKey();
        String oldLargeImageFileKey = original.getLargeImageFileKey();
        if ((null == newLargeImageFileKey && null != oldLargeImageFileKey)
                || (null != newLargeImageFileKey && !newLargeImageFileKey.equals(original.getLargeImageFileKey()))) {
            original.setLargeImageFileKey(newLargeImageFileKey);
            commentWasUpdated = true;
        }

        String newSmallImageFileKey = updates.getSmallImageFileKey();
        String oldSmallImageFileKey = original.getSmallImageFileKey();
        if ((null == newSmallImageFileKey && null != oldSmallImageFileKey)
                || (null != newSmallImageFileKey && !newSmallImageFileKey.equals(original.getSmallImageFileKey()))) {
            original.setSmallImageFileKey(newSmallImageFileKey);
            commentWasUpdated = true;
        }

        return commentWasUpdated;
    }
}
