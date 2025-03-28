package faang.school.postservice.service;

import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Сервис для работы с комментариями.
 * <p>
 * Основные методы:
 * <ul>
 *   <li>{@link #getCommentEntity(long)} - возвращает сущность комментария по ID</li>
 * </ul>
 *
 * @author gulnaz21
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {
    private final CommentRepository commentRepository;

    /**
     * Возвращает комментарий по его идентификатору.
     *
     * @param commentId Идентификатор комментария.
     * @return Найденный комментарий.
     * @throws EntityNotFoundException Если комментарий с указанным идентификатором не найден.
     */
    public Comment getCommentEntity(long commentId) {
        return commentRepository.findById(commentId).orElseThrow(() -> {
            log.error("Ошибка: Комментарий с id {} не найден", commentId);
            return new EntityNotFoundException(String.format("Comment not found with id: %d", commentId));
        });
    }
}
