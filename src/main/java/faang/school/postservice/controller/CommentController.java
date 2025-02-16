package faang.school.postservice.controller;

import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.service.post.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public CommentDto createComment(@Valid @RequestBody CommentDto commentDto ,PostDto postDto){
        log.info("Received request to create comment from User ID: {} to Post ID: {} .",commentDto.getId(),postDto.getId());
        return commentService.createComment(commentDto);
    }
    @PutMapping("/{id}")
    public CommentDto updateComment(@PathVariable Long id,@RequestBody CommentDto commentDto){
        log.info("Received request to update comment ID: {} .", id);
        return commentService.updateComment(id,commentDto);
    }
    @GetMapping("/all")
    public List<CommentDto> getAllComments(){
        log.info("Received request to retrieve all comments");
        return commentService.getAllComments();
    }
    @GetMapping("/{postId}")
    public List<CommentDto> getCommentsByPostId(@PathVariable Long postId) {
        log.info("Received request to retrieve all comments for Post ID: {}.", postId);
        return commentService.getCommentsByPostId(postId);
    }
    @DeleteMapping("/{id}")
    public void deleteCommentById(@PathVariable Long id) {
        log.info("Received request to delete comment with ID: {}.", id);
        commentService.deleteCommentById(id);
    }
}
