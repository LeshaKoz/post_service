package faang.school.postservice.controller;

import faang.school.postservice.dto.comment.CommentForListDto;
import faang.school.postservice.dto.comment.CreateCommentRequest;
import faang.school.postservice.dto.comment.CreateCommentResponse;
import faang.school.postservice.dto.comment.UpdateCommentRequest;
import faang.school.postservice.dto.comment.UpdatedCommentResponse;
import faang.school.postservice.model.Comment;
import faang.school.postservice.service.CommentService;
import lombok.RequiredArgsConstructor;
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

@RequiredArgsConstructor
@RequestMapping("/comment")
@Validated
@RestController
public class CommentController {

    private final CommentService commentService;


    @PostMapping
    public CreateCommentResponse createComment(@RequestBody CreateCommentRequest createCommentRequest) {
        return commentService.createComment(createCommentRequest);
    }

    @PutMapping
    public UpdatedCommentResponse updateComment(@RequestBody UpdateCommentRequest updateCommentRequest) {
        return commentService.updateComment(updateCommentRequest);
    }

    @GetMapping("/getComments/{postId}")
    public List<CommentForListDto> getListComment(@PathVariable Long postId) {
        return commentService.getListComment(postId);
    }

    @DeleteMapping("/delete/{commentId}")
    public void deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
    }
}
