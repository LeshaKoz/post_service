package faang.school.postservice.validation;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostCreateDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
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
 * </ul>
 * </p>
 *
 * @author marsel_mkh
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PostValidator {
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
            throw new DataValidationException("Post cannot be missing an author and a project");
        }

        if (authorId != null) {
            UserDto author = userServiceClient.getUser(authorId);
            if (author == null) {
                log.error("Validation failed: Author with ID {} does not exist", authorId);
                throw new EntityNotFoundException("Author does not exist");
            }
        } else {
            ProjectDto project = projectServiceClient.getProject(projectId);
            if (project == null) {
                log.error("Validation failed: Project with ID {} does not exist", projectId);
                throw new EntityNotFoundException("Project does not exist");
            }
        }
    }
}
