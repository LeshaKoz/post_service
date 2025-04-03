package faang.school.postservice.controller.comment;

import faang.school.postservice.dto.comment.CommentCreateDto;
import faang.school.postservice.dto.comment.CommentUpdateDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createComment(@Valid @RequestBody CommentCreateDto commentCreateDto) {
        log.info("Received comment creation request. Data: {}", commentCreateDto);
        return commentService.createComment(commentCreateDto);
    }

    @GetMapping("/post/{postId}")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> getAllCommentsByPostId(@PathVariable long postId) {
        log.info("Received request to fetch comments for post ID: {}", postId);
        return commentService.getAllCommentsByPostId(postId);
    }

    @PutMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto updateComment(
            @PathVariable long commentId,
            @Valid @RequestBody CommentUpdateDto commentUpdateDto
    ) {
        log.info("Received request to update comment with ID: {}", commentId);
        return commentService.updateComment(commentId, commentUpdateDto);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(
            @PathVariable @Min(1) long commentId,
            @RequestParam @Min(1) long authorId) {
        log.info("Received request to delete comment with ID: {}", commentId);
        commentService.deleteComment(commentId, authorId);
    }
}