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
        log.debug("Creating comment for post with ID: {}", postId);
        Post post = getPostById(postId);
        validateUserById(commentCreateDto.getAuthorId());

        Comment comment = commentMapper.toEntity(commentCreateDto);
        comment.setPost(post);
        comment.setCreatedAt(LocalDateTime.now());

        Comment savedComment = commentRepository.save(comment);
        log.debug("Comment with ID: {} successfully created", savedComment.getId());

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
        log.debug("Updating comment with ID: {} for post with ID: {}", commentId, postId);
        Comment comment = getCommentById(commentId);
        validateCommentBelongsToPost(comment, postId, commentId);

        comment.setContent(commentCreateDto.getContent());
        comment.setUpdatedAt(LocalDateTime.now());

        Comment updatedComment = commentRepository.save(comment);
        log.debug("Comment with ID: {} successfully updated", updatedComment.getId());

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
        log.debug("Retrieving comments for post with ID: {}", postId);
        List<Comment> comments = commentRepository.findAllByPostId(postId);
        log.info("Found {} comments for post with ID: {}", comments.size(), postId);

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
        log.debug("Deleting comment with ID: {} for post with ID: {}", commentId, postId);
        Comment comment = getCommentById(commentId);
        validateCommentBelongsToPost(comment, postId, commentId);

        commentRepository.delete(comment);
        log.debug("Comment with ID: {} successfully deleted", commentId);
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
                .orElseThrow(() -> new EntityNotFoundException("Comment with ID " + commentId + " not found"));
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
            throw new DataValidationException("Comment with ID " + commentId
                    + " doesn't belong to post with ID " + postId);
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
                .orElseThrow(() -> new EntityNotFoundException("Post with ID " + postId + " not found"));
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
            log.error("User with ID {} not found", authorId);
            throw new EntityNotFoundException("User with ID " + authorId + " not found");
        }
    }
}
