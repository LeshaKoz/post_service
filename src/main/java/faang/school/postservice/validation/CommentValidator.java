package faang.school.postservice.validation;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Валидатор для проверки корректности данных комментариев.
 * Содержит методы валидации перед выполнением операций с комментариями.
 *
 * <p>Основные методы валидации:</p>
 * <ul>
 *   <li>{@link #validateCommentBelongsToPost(Comment, Long, Long)} - Проверка принадлежности комментария посту</li>
 *   <li>{@link #validateUserById(Long)} - Проверка существования пользователя</li>
 * </ul>
 *
 * @author Zhltsk-V
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
public class CommentValidator {
    private final UserServiceClient userServiceClient;
    private final PostRepository postRepository;;

    /**
     * Проверяет принадлежность комментария указанному посту.
     *
     * @param comment проверяемый комментарий
     * @param postId ожидаемый ID поста
     * @param commentId ID комментария для сообщения об ошибке
     * @throws DataValidationException если комментарий не принадлежит указанному посту
     *
     */
    public void validateCommentBelongsToPost(Comment comment, Long postId, Long commentId) {
        if (comment == null || !comment.belongsToPost(postId)) {
            throw new DataValidationException("Comment with ID " + commentId
                    + " doesn't belong to post with ID " + postId);
        }
    }

    /**
     * Проверяет существование пользователя в системе.
     *
     * @param authorId ID пользователя для проверки
     * @throws EntityNotFoundException если пользователь с указанным ID не найден
     *
     */
    public void validateUserById(Long authorId) {
        UserDto userDto = userServiceClient.getUser(authorId);
        if (userDto == null) {
            throw new EntityNotFoundException("User with ID " + authorId + " not found");
        }
    }

    /**
     * Проверяет существование поста с указанным идентификатором.
     *
     * @param postId идентификатор поста для проверки
     * @throws EntityNotFoundException если пост с указанным ID не найден
     * @throws IllegalArgumentException если переданный postId равен null
     */
    public void validatePostExists(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new EntityNotFoundException("Post with ID " + postId + " not found");
        }
    }
}
