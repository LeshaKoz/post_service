package faang.school.postservice.service.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentCreateDto;
import faang.school.postservice.dto.comment.CommentViewDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Сервис для работы с комментариями.
 * Содержит бизнес-логику для создания, обновления, получения и удаления комментариев.
 *
 * @author Zhltsk-V
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final CommentMapper commentMapper;
    private final UserServiceClient userServiceClient;

    /**
     * Создает новый комментарий.
     *
     * @param postId           Идентификатор поста, к которому относится комментарий.
     * @param commentCreateDto DTO с данными для создания комментария.
     * @return Созданный комментарий в формате CommentViewDto.
     * @throws EntityNotFoundException Если пост или пользователь не найдены.
     */
    @Transactional
    public CommentViewDto createComment(Long postId, CommentCreateDto commentCreateDto) {
        log.debug("Создание комментария для поста с ID: {}", postId);
        Post post = getPostById(postId);
        validateUserById(commentCreateDto.getAuthorId());

        Comment comment = commentMapper.toEntity(commentCreateDto);
        comment.setPost(post);
        comment.setCreatedAt(LocalDateTime.now());

        Comment savedComment = commentRepository.save(comment);
        log.debug("Комментарий с ID: {} успешно создан", savedComment.getId());

        return commentMapper.toViewDto(savedComment);
    }

    /**
     * Обновляет текст комментария.
     *
     * @param postId           Идентификатор поста, к которому относится комментарий.
     * @param commentId        Идентификатор комментария, который нужно обновить.
     * @param commentCreateDto DTO с новым текстом комментария.
     * @return Обновленный комментарий в формате CommentViewDto.
     * @throws EntityNotFoundException Если комментарий не найден.
     * @throws DataValidationException Если комментарий не принадлежит указанному посту.
     */
    @Transactional
    public CommentViewDto updateComment(Long postId, Long commentId, CommentCreateDto commentCreateDto) {
        log.debug("Обновление комментария с ID: {} для поста с ID: {}", commentId, postId);
        Comment comment = getCommentById(commentId);
        validateCommentBelongsToPost(comment, postId, commentId);

        comment.setContent(commentCreateDto.getContent());
        comment.setUpdatedAt(LocalDateTime.now());

        Comment updatedComment = commentRepository.save(comment);
        log.debug("Комментарий с ID: {} успешно обновлен", updatedComment.getId());

        return commentMapper.toViewDto(updatedComment);
    }

    /**
     * Возвращает список всех комментариев для указанного поста.
     *
     * @param postId Идентификатор поста.
     * @return Список комментариев в формате CommentViewDto.
     */
    @Transactional
    public List<CommentViewDto> getCommentsByPostId(Long postId) {
        log.debug("Получение комментариев для поста с ID: {}", postId);
        List<Comment> comments = commentRepository.findAllByPostId(postId);
        log.info("Найдено {} комментариев для поста с ID: {}", comments.size(), postId);

        return comments.stream()
                .map(commentMapper::toViewDto)
                .toList();
    }

    /**
     * Удаляет комментарий по его идентификатору.
     *
     * @param postId    Идентификатор поста, к которому относится комментарий.
     * @param commentId Идентификатор комментария, который нужно удалить.
     * @throws EntityNotFoundException Если комментарий не найден.
     * @throws DataValidationException Если комментарий не принадлежит указанному посту.
     */
    @Transactional
    public void deleteComment(Long postId, Long commentId) {
        log.debug("Удаление комментария с ID: {} для поста с ID: {}", commentId, postId);
        Comment comment = getCommentById(commentId);
        validateCommentBelongsToPost(comment, postId, commentId);

        commentRepository.delete(comment);
        log.debug("Комментарий с ID: {} успешно удален", commentId);
    }

    /**
     * Получает комментарий по его идентификатору.
     *
     * @param commentId Идентификатор комментария.
     * @return Найденный комментарий.
     * @throws EntityNotFoundException Если комментарий не найден.
     */
    private Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Комментарий с ID " + commentId + " не найден"));
    }

    /**
     * Проверяет, что комментарий принадлежит указанному посту.
     *
     * @param comment   Комментарий для проверки.
     * @param postId    Идентификатор поста.
     * @param commentId Идентификатор комментария (для сообщения об ошибке).
     * @throws DataValidationException Если комментарий не принадлежит указанному посту.
     */
    private void validateCommentBelongsToPost(Comment comment, Long postId, Long commentId) {
        if (!comment.getPost().getId().equals(postId)) {
            throw new DataValidationException("Комментарий с ID " + commentId
                    + " не принадлежит посту с ID " + postId);
        }
    }

    /**
     * Получает пост по его идентификатору.
     *
     * @param postId Идентификатор поста.
     * @return Найденный пост.
     * @throws EntityNotFoundException Если пост не найден.
     */
    private Post getPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Пост с ID " + postId + " не найден"));

    }

    /**
     * Проверяет существование пользователя по его идентификатору.
     *
     * @param authorId Идентификатор пользователя.
     * @throws EntityNotFoundException Если пользователь не найден.
     */
    private void validateUserById(Long authorId) {
        UserDto userDto = userServiceClient.getUser(authorId);
        if (userDto == null) {
            log.error("Пользователь с ID {} не найден", authorId);
            throw new EntityNotFoundException("Пользователь с ID " + authorId + " не найден");
        }
    }
}
