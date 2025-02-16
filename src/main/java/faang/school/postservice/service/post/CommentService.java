package faang.school.postservice.service.post;

import faang.school.postservice.dto.CommentDto;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {

    // In-memory storage (replace with database repository in real implementation)
    private final List<CommentDto> commentStorage = new ArrayList<>();
    private long commentIdCounter = 1; // Simulates auto-generated IDs

    public CommentDto createComment(CommentDto commentDto) {
        commentDto.setId(commentIdCounter++);
        commentDto.setCreatedAt(LocalDateTime.now());
        commentDto.setUpdatedAt(LocalDateTime.now());
        commentStorage.add(commentDto);
        return commentDto;
    }

    public CommentDto updateComment(Long id, CommentDto updatedCommentDto) {
        for (CommentDto comment : commentStorage) {
            if (comment.getId().equals(id)) {
                comment.setContent(updatedCommentDto.getContent());
                comment.setUpdatedAt(LocalDateTime.now());
                return comment;
            }
        }
        throw new IllegalArgumentException("Comment with ID " + id + " not found.");
    }

    public List<CommentDto> getCommentsByPostId(Long postId) {
        return commentStorage.stream()
                .filter(comment -> comment.getPostId().equals(postId))
                .collect(Collectors.toList());
    }

    public List<CommentDto> getAllComments() {
        return new ArrayList<>(commentStorage);
    }
    public void deleteCommentById(Long id) {
        boolean removed = commentStorage.removeIf(comment -> comment.getId().equals(id));
        if (!removed) {
            throw new IllegalArgumentException("Comment with ID " + id + " not found.");
        }
    }
}
