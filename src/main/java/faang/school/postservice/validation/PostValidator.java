package faang.school.postservice.validation;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostCreateDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Класс `PostValidator` выполняет валидацию данных, связанных с постами.
 * Проверяет существование авторов и проектов при создании поста,
 * а также корректность изменения данных при обновлении поста.
 *
 * <p>Основные методы:
 * <ul>
 *     <li>{@link #validateAuthorAndProject(PostCreateDto)} - проверяет существование автора и проекта.</li>
 *     <li>{@link #validateAuthor(PostUpdateDto, long)} - проверяет, не изменился ли автор поста.</li>
 * </ul>
 * </p>
 *
 * @author marsel_mkh
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PostValidator {
    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;

    /**
     * Проверяет существование автора или проекта перед созданием поста.
     * Если оба значения отсутствуют или не существуют в системе, выбрасывает исключение.
     *
     * @param post объект DTO для создания поста
     * @throws DataValidationException если автор и проект не указаны
     * @throws EntityNotFoundException если указанный автор или проект не найдены
     */
    public void validateAuthorAndProject(PostCreateDto post) {
        Long authorId = post.getAuthorId();
        Long projectId = post.getProjectId();

        if (authorId == null && projectId == null) {
            log.warn("Validation failed: Both author and project are missing in the post");
            throw new DataValidationException("Author or Project in the post is not found");
        }

        if(authorId != null){
            if(userServiceClient.getUser(authorId) == null){
                log.error("Validation failed: Author with ID {} does not exist", authorId);
                throw new EntityNotFoundException("Author does not exist");
            }
        } else {
            if(projectServiceClient.getProject(projectId) == null){
                log.error("Validation failed: Project with ID {} does not exist", projectId);
                throw new EntityNotFoundException("Project does not exist");
            }
        }
    }

    /**
     * Проверяет, не изменился ли автор поста при обновлении.
     * Если новый идентификатор автора не совпадает с текущим, выбрасывает исключение.
     *
     * @param postUpdateDto объект DTO для обновления поста
     * @param postId идентификатор поста
     * @throws EntityNotFoundException если пост не найден
     * @throws DataValidationException если автор поста изменился
     */
    public void validateAuthor(PostUpdateDto postUpdateDto, long postId) {
        Post oldPost = postRepository.findById(postId).orElseThrow(() -> {
            log.error("Validation failed: Post with ID {} not found", postId);
            return new EntityNotFoundException("Post not found with id: " + postId);
        });

        Long oldAuthorId = oldPost.getAuthorId();
        Long newAuthorId = postUpdateDto.getAuthorId();

        if (!newAuthorId.equals(oldAuthorId)) {
            log.warn("Validation failed: Author mismatch. Old ID: {}, New ID: {}", oldAuthorId, newAuthorId);
            throw new DataValidationException("Authors should not differ");
        }
    }
}
