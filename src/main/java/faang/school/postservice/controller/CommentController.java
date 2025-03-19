package faang.school.postservice.controller;

import faang.school.postservice.dto.comment.CommentCreateDto;
import faang.school.postservice.dto.comment.CommentResponseDto;
import faang.school.postservice.dto.comment.CommentUpdateDto;
import faang.school.postservice.service.CommentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comment")
@Validated
@Slf4j
public class CommentController {

    private final CommentService commentService;

    @PostMapping(value = "/create")
    public ResponseEntity<Void> createComment(@RequestBody @Valid CommentCreateDto commentCreateDto) {
        log.info("Received request to add comment: {}", commentCreateDto);
        commentService.createComment(commentCreateDto);
        log.info("Comment successfully added cre");

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping(value = "/update/content")
    public ResponseEntity<Void> updateCommentContent(@RequestBody @Valid CommentUpdateDto commentUpdateDto) {
        log.info("Received request to update comment content: {}", commentUpdateDto);
        commentService.updateCommentContent(commentUpdateDto);
        log.info("Comment successfully updated");

        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/get/{postId}")
    public ResponseEntity<List<CommentResponseDto>> getAllComments(@PathVariable @Min(1) long postId) {
        log.debug("Fetching all comments for post with ID: {}", postId);
        ResponseEntity<List<CommentResponseDto>> response = commentService.getAllComments(postId);
        log.info("Fetched all comments for post with ID: {}", postId);
        return response;
    }

    @DeleteMapping("/delete/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable @Min(1) long commentId) {
        log.info("Received request to delete comment with ID: {}", commentId);
        commentService.deleteComment(commentId);
        log.info("Comment with ID {} successfully deleted", commentId);

        return ResponseEntity.noContent().build();
    }
}
