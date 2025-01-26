package faang.school.postservice.controller.comment;

import faang.school.postservice.dto.comment.CommentRequestDto;
import faang.school.postservice.dto.comment.CommentResponseDto;
import faang.school.postservice.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/api/v1/comment")
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    public CommentResponseDto createComment(@RequestBody CommentRequestDto commentRequestDto) {
        return commentService.createComment(commentRequestDto);
    }

    @DeleteMapping("/{commentId}")
    public void deleteComment(@PathVariable long commentId) {
        commentService.deleteComment(commentId);
    }

    @PutMapping("/{commentId}")
    public void updateComment(@PathVariable long commentId,
                              @RequestBody CommentRequestDto commentRequestDto) {
        commentService.updateComment(commentId, commentRequestDto);
    }

    @GetMapping("/{postId}")
    public List<CommentResponseDto> getCommentsByPostId(@PathVariable long postId) {
        return commentService.getComments(postId);
    }
}
