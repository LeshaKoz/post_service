package faang.school.postservice.controller;

import faang.school.postservice.dto.comment.CommentCreateDto;
import faang.school.postservice.dto.comment.CommentReadDto;
import faang.school.postservice.dto.comment.CommentUpdateDto;
import faang.school.postservice.service.CommentService;
import faang.school.postservice.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
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

@RequestMapping("/comments")
@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "Добавление комментария",
            description = "Создаёт новый комментарий для указанного поста")
    @PostMapping
    public CommentReadDto addComment(@Valid @RequestBody CommentCreateDto commentCreateDto) {
        return commentService.addComment(commentCreateDto);
    }
    @Operation(summary = "Редактирование комментария",
            description = "Редактирует существующий комментарий")
    @PutMapping
    public CommentReadDto editComment(@Valid @RequestBody CommentUpdateDto commentUpdateDto) {
        return commentService.editComment(commentUpdateDto);
    }

    @Operation(summary = "Получение комментария к посту",
            description = "Возвращает список всех комментариев для указанного поста")
    @GetMapping
    public List<CommentReadDto> getComments(@PathVariable @Valid @Positive long postId) {
        return commentService.getComments(postId);
    }

    @Operation(summary = "Удаление комментария",
            description = "Удаляет указанный комментарий из поста")
    @DeleteMapping("/{commentId}")
    public void deleteCommentFromPost(@PathVariable @Valid @Positive long commentId) {
        commentService.deleteComment(commentId);
    }
}
