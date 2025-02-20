package faang.school.postservice.controller.post;

import faang.school.postservice.dto.post.CommentDto;
import faang.school.postservice.service.post.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Комментарии")
@RestController
@RequiredArgsConstructor
@RequestMapping("${domain.path}/comments")
@Validated
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "Создать комментарий")
    @PostMapping
    public CommentDto createComment(@RequestBody CommentDto dto) {
        return commentService.createComment(dto);
    }

    @Operation(summary = "Обновить комментарий")
    @PutMapping
    public CommentDto updateComment(@RequestBody CommentDto dto) {
        return commentService.updateComment(dto);
    }

    @Operation(summary = "Найти все комментарии по автору")
    @GetMapping("/post/{id}")
    public List<CommentDto> findAllCommentsByAuthor(@PathVariable long postId) {
        return commentService.findAllCommentsByPostId(postId);
    }

    @Operation(summary = "Получить комментарий по id")
    @DeleteMapping("/{id}")
    public void deleteCommentById(@PathVariable long id) {
        commentService.deleteCommentById(id);
    }
}
