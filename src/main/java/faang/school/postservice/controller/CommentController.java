package faang.school.postservice.controller;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    @Autowired
    private  CommentService service;

    @PostMapping("/create/{userId}/{postId}")
    public CommentDto createComment(
            @PathVariable long userId, @PathVariable long postId, @RequestBody CommentDto commentDto) {
        return service.createComment(userId, postId, commentDto);
    }

    @PutMapping("/edit/{commentId}/{content}")
    public CommentDto editComment(
            @RequestBody CommentDto commentDto, @PathVariable long commentId, @PathVariable String content) {
        return service.editComment(commentDto, commentId, content);
    }

    @GetMapping("/{postId}")
    public List<CommentDto> getAllComments(@PathVariable long postId) {
        return service.getAllComments(postId);
    }

    @DeleteMapping("/delete/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable long commentId) {
        service.deleteComment(commentId);
    }
}

