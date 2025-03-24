package faang.school.postservice.controller.comment;

import faang.school.postservice.dto.comment.CommentCreateDto;
import faang.school.postservice.dto.comment.CommentViewDto;
import faang.school.postservice.service.comment.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Контроллер для обработки запросов, связанных с комментариями.
 * Предоставляет REST-эндпоинты для создания, обновления, получения и удаления комментариев.
 *
 * @author Zhltsk-V
 * @version 1.0
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/posts/{postId}/comments")
public class CommentController {
    private final CommentService commentService;

    /**
     * Создает новый комментарий для указанного поста.
     *
     * @param postId           Идентификатор поста, к которому относится комментарий.
     * @param commentCreateDto DTO с данными для создания комментария.
     * @return Ответ с созданным комментарием в формате CommentViewDto и статусом HTTP 201 (Created).
     */
    @PostMapping
    public ResponseEntity<CommentViewDto> createComment(@PathVariable long postId,
                                                        @RequestBody @Valid CommentCreateDto commentCreateDto) {
        log.info("Запрос на создание комментария для поста с ID: {}", postId);
        CommentViewDto createdComment = commentService.createComment(postId, commentCreateDto);
        log.info("Комментарий успешно создан с ID: {}", createdComment.getId());
        return ResponseEntity.ok(createdComment);
    }

    /**
     * Обновляет текст существующего комментария.
     *
     * @param postId           Идентификатор поста, к которому относится комментарий.
     * @param commentId        Идентификатор комментария, который нужно обновить.
     * @param commentCreateDto DTO с новым текстом комментария.
     * @return Ответ с обновленным комментарием в формате CommentViewDto и статусом HTTP 200 (OK).
     */
    @PutMapping("/{commentId}")
    public ResponseEntity<CommentViewDto> updateComment(@PathVariable long postId,
                                                        @PathVariable long commentId,
                                                        @RequestBody @Valid CommentCreateDto commentCreateDto) {
        log.info("Запрос на обновление комментария с ID: {} для поста с ID: {}", commentId, postId);
        CommentViewDto updatedComment = commentService.updateComment(postId, commentId, commentCreateDto);
        log.info("Комментарий с ID: {} успешно обновлен", commentId);
        return ResponseEntity.ok(updatedComment);
    }

    /**
     * Возвращает список всех комментариев для указанного поста.
     * Комментарии сортируются по дате создания (от самого позднего к самому раннему).
     *
     * @param postId Идентификатор поста.
     * @return Ответ со списком комментариев в формате CommentViewDto и статусом HTTP 200 (OK).
     */
    @GetMapping
    public ResponseEntity<List<CommentViewDto>> getCommentsByPostId(@PathVariable long postId) {
        log.info("Запрос на получение комментариев для поста с ID: {}", postId);
        List<CommentViewDto> comments = commentService.getCommentsByPostId(postId);
        log.info("Найдено {} комментариев для поста с ID: {}", comments.size(), postId);
        return ResponseEntity.ok(comments);
    }

    /**
     * Удаляет комментарий по его идентификатору.
     *
     * @param postId    Идентификатор поста, к которому относится комментарий.
     * @param commentId Идентификатор комментария, который нужно удалить.
     * @return Ответ со статусом HTTP 204 (No Content), если удаление прошло успешно.
     */
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable long postId,
                                              @PathVariable long commentId) {
        log.info("Запрос на удаление комментария с ID: {} для поста с ID: {}", commentId, postId);
        commentService.deleteComment(postId, commentId);
        log.info("Комментарий с ID: {} успешно удален", commentId);
        return ResponseEntity.noContent().build();
    }
}