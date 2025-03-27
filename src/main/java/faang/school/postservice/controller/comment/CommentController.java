package faang.school.postservice.controller.comment;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@Validated
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentDto> createComment(@Valid @RequestBody CommentDto commentDto) {

        CommentDto createdComment = commentService.createComment(commentDto);
        log.info("Received comment creation request. Data: {}", commentDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdComment);
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<CommentDto>> getAllCommentsByPostId(@PathVariable long postId) {
        log.info("Received request to fetch comments for post ID: {}", postId);
        List<CommentDto> comments = commentService.getAllCommentsByPostId(postId);
        return ResponseEntity.ok(comments);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentDto> updateComment(
            @PathVariable long commentId,
            @Valid @RequestBody CommentDto commentDto
    ) {
        log.info("Received request to update comment with ID: {}", commentId);
        return ResponseEntity.ok(commentService.updateComment(commentId, commentDto));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment( @PathVariable @Min (1) long commentId) {
        log.info("Received request to delete comment with ID: {}", commentId);
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
}