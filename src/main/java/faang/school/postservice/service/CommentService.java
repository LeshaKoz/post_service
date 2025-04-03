package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentCreateDto;
import faang.school.postservice.dto.comment.CommentViewDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validation.CommentValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Сервис для работы с комментариями.
 * Содержит бизнес-логику для управления комментариями.
 *
 * <p>Основные методы:</p>
 * <ul>
 *   <li>{@link #createComment(Long, CommentCreateDto)} - Создание нового комментария</li>
 *   <li>{@link #updateComment(Long, Long, CommentCreateDto)} - Обновление существующего комментария</li>
 *   <li>{@link #getCommentsByPostId(Long)} - Получение всех комментариев поста</li>
 *   <li>{@link #deleteComment(Long, Long)} - Удаление комментария</li>
 * </ul>
 *
 * <p>Вспомогательные методы:</p>
 * <ul>
 *   <li>{@link #getCommentById(Long)} - Получение комментария по ID</li>
 *   <li>{@link #getPostById(Long)} - Получение поста по ID</li>
 * </ul>
 *
 * @see CommentValidator Для методов валидации
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
    private final CommentValidator commentValidator;

    /**
     * Создает новый комментарий к указанному посту.
     *
     * @param postId ID поста для комментария
     * @param commentCreateDto DTO с данными для создания комментария
     * @return созданный комментарий в формате CommentViewDto
     * @throws EntityNotFoundException если пост не найден или пользователь не существует
     *
     * @see CommentValidator#validateUserById(Long)
     */
    @Transactional
    public CommentViewDto createComment(Long postId, CommentCreateDto commentCreateDto) {
        log.debug("Creating comment for post with ID: {}", postId);

        commentValidator.validatePostExists(postId);
        commentValidator.validateUserById(commentCreateDto.getAuthorId());

        Comment comment = commentMapper.toEntity(commentCreateDto);
        comment.setPost(getPostById(postId));
        comment.setCreatedAt(LocalDateTime.now());

        Comment savedComment = commentRepository.save(comment);
        log.debug("Comment with ID: {} successfully created", savedComment.getId());

        return commentMapper.toViewDto(savedComment);
    }

    /**
     * Обновляет существующий комментарий.
     *
     * @param postId ID поста, содержащего комментарий
     * @param commentId ID комментария для обновления
     * @param commentCreateDto DTO с обновленными данными комментария
     * @return обновленный комментарий в формате CommentViewDto
     * @throws EntityNotFoundException если комментарий не найден
     * @throws DataValidationException если комментарий не принадлежит указанному посту
     *
     * @see CommentValidator#validateCommentBelongsToPost(Comment, Long, Long)
     */
    @Transactional
    public CommentViewDto updateComment(Long postId, Long commentId, CommentCreateDto commentCreateDto) {
        log.debug("Updating comment with ID: {} for post with ID: {}", commentId, postId);
        Comment comment = getCommentById(commentId);
        commentValidator.validateCommentBelongsToPost(comment, postId, commentId);

        comment.setContent(commentCreateDto.getContent());
        comment.setUpdatedAt(LocalDateTime.now());

        Comment updatedComment = commentRepository.save(comment);
        log.debug("Comment with ID: {} successfully updated", updatedComment.getId());

        return commentMapper.toViewDto(updatedComment);
    }

    /**
     * Получает все комментарии для указанного поста.
     *
     * @param postId ID поста для получения комментариев
     * @return список комментариев в формате CommentViewDto
     */
    @Transactional
    public List<CommentViewDto> getCommentsByPostId(Long postId) {
        log.debug("Retrieving comments for post with ID: {}", postId);
        commentValidator.validatePostExists(postId);

        List<Comment> comments = commentRepository.findAllByPostId(postId);
        log.info("Found {} comments for post with ID: {}", comments.size(), postId);

        return comments.stream()
                .map(commentMapper::toViewDto)
                .toList();
    }

    /**
     * Удаляет указанный комментарий.
     *
     * @param postId ID поста, содержащего комментарий
     * @param commentId ID комментария для удаления
     * @throws EntityNotFoundException если комментарий не найден
     * @throws DataValidationException если комментарий не принадлежит указанному посту
     *
     * @see CommentValidator#validateCommentBelongsToPost(Comment, Long, Long)
     */
    @Transactional
    public void deleteComment(Long postId, Long commentId) {
        log.debug("Deleting comment with ID: {} for post with ID: {}", commentId, postId);
        Comment comment = getCommentById(commentId);
        commentValidator.validateCommentBelongsToPost(comment, postId, commentId);

        commentRepository.delete(comment);
        log.debug("Comment with ID: {} successfully deleted", commentId);
    }

    /**
     * Получает комментарий по его идентификатору.
     *
     * @param commentId ID комментария
     * @return найденный комментарий
     * @throws EntityNotFoundException если комментарий не найден
     */
    public Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> {
                    log.error("Comment with ID {} not found", commentId);
                    return new EntityNotFoundException("Comment with ID " + commentId + " not found");
                });
    }

    /**
     * Получает пост по его идентификатору.
     *
     * @param postId ID поста
     * @return найденный пост
     * @throws EntityNotFoundException если пост не найден
     */
    public Post getPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post with ID " + postId + " not found"));
    }

    /**
     * Обновляет сущность комментария и возвращает её DTO-представление.
     * Используется для случаев, когда обновление происходит не через стандартный DTO.
     *
     * @param comment сущность комментария для обновления
     * @return DTO-представление обновленного комментария
     */
    public CommentViewDto updateCommentEntity(Comment comment) {
        Comment updatedComment = commentRepository.save(comment);
        return commentMapper.toViewDto(updatedComment);
    }
}