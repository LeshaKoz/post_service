package faang.school.postservice.controller;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.service.CommentService;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
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
@RequestMapping("/api/v1/comment")
@RequiredArgsConstructor
public class CommentController {

    private static final String ID_ERROR_MESSAGE = "ID must be positive";

    private final CommentService commentService;
    private final CommentMapper commentMapper;
    private final UserContext userContext;


    @GetMapping(value = "/post/{postId}")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> getComments(@Positive(message = ID_ERROR_MESSAGE) @PathVariable Long postId) {

        return commentService.getComments(postId).stream()
                .map(commentMapper::toDto)
                .toList();
    }

    @GetMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto getComment(@Positive(message = ID_ERROR_MESSAGE)
                                     @PathVariable Long commentId) {

        return commentMapper.toDto(
                commentService.getComment(commentId));
    }

    @PostMapping("/post/{postId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createComment(@Positive(message = ID_ERROR_MESSAGE) @PathVariable Long postId
            , @Validated @NotEmpty @RequestBody CommentDto commentDto) {

        Comment comment = commentMapper.toEntity(commentDto);
        return commentMapper.toDto(
                commentService.createComment(postId, comment));
    }

    @PutMapping("/{commentId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CommentDto updateComment(@Positive(message = ID_ERROR_MESSAGE) @PathVariable Long commentId
            , @NotNull @RequestBody CommentDto editedCommentDto) {

        Comment editedComment = commentMapper.toEntity(editedCommentDto);
        return commentMapper.toDto(
                commentService.updateComment(commentId, editedComment));
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public boolean deleteComment(@Positive(message = ID_ERROR_MESSAGE) @PathVariable Long commentId) {

        return commentService.deleteComment(commentId);
    }
}
