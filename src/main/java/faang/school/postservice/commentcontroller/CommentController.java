package faang.school.postservice.commentcontroller;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.service.CommentService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/comments")
public class CommentController {
    private CommentService service;

    @PostMapping("/create")
    public CommentDto createComment(
            @RequestParam long userId, @RequestParam long postId, @RequestBody CommentDto commentDto) {
        return service.createComment(userId, postId, commentDto);
    }

    @PutMapping("/edit/{commentId}")
    public CommentDto editComment(
            @RequestBody CommentDto commentDto, @RequestParam long commentId, @RequestParam String content) {
        return service.editComment(commentDto, commentId, content);
    }

    @GetMapping("/post/{postId}")
    public List<CommentDto> getAllComments(@RequestParam long postId) {
        return service.getAllComments(postId);
    }

    @DeleteMapping("/delete/{commentId}")
    public void deleteComment(@RequestBody CommentDto commentDto, @RequestParam long commentId) {
        service.deleteComment(commentDto, commentId);
    }
}
