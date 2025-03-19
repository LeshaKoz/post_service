package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostCreateDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.dto.post.PostViewDto;
import faang.school.postservice.service.PostService;
import faang.school.postservice.validation.PostValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Контроллер для управления постами.
 * <p>
 * Предоставляет API для выполнения операций с постами, таких как:
 * <ul>
 *     <li>{@link #createDraft(PostCreateDto)}: Создание черновика поста.</li>
 *     <li>{@link #publishPost(long)}: Публикация поста.</li>
 *     <li>{@link #updatePost(PostUpdateDto, long)}: Обновление поста.</li>
 *     <li>{@link #softDeletePost(long)}: Мягкое удаление поста.</li>
 *     <li>{@link #getPost(long)}: Получение поста по его ID.</li>
 *     <li>{@link #getUserDraft(long)}: Получение черновиков постов для пользователя.</li>
 *     <li>{@link #getProjectDraft(long)}: Получение черновиков постов для проекта.</li>
 *     <li>{@link #getAuthorPublishedPost(long)}: Получение опубликованных постов для пользователя.</li>
 *     <li>{@link #getProjectPublishedPost(long)}: Получение опубликованных постов для проекта.</li>
 * </ul>
 * <p>
 * Все методы контроллера используют DTO (Data Transfer Objects) для передачи данных между клиентом и сервером.
 * Валидация входных данных выполняется с использованием аннотаций {@link Valid} и кастомного валидатора {@link PostValidator}.
 * <p>
 * Логирование операций выполняется с использованием библиотеки Lombok ({@link Slf4j}).
 *
 * @author marsel_mkh
 * @see PostService
 * @see PostValidator
 * @see PostCreateDto
 * @see PostUpdateDto
 * @see PostViewDto
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final PostValidator postValidator;

    /**
     * Создает черновик поста.
     *
     * @param postCreateDto DTO с данными для создания поста.
     * @return ResponseEntity с созданным постом в формате PostViewDto.
     */
    @PostMapping
    public ResponseEntity<PostViewDto> createDraft(@Valid @RequestBody PostCreateDto postCreateDto) {
        log.info("Received request to create a draft post: {}", postCreateDto);

        postValidator.validateAuthorAndProject(postCreateDto);
        PostViewDto postViewDto = postService.createDraft(postCreateDto);

        log.info("Draft post created successfully: {}", postViewDto);
        return ResponseEntity.ok(postViewDto);
    }

    /**
     * Публикует пост по его ID.
     *
     * @param postId ID поста для публикации.
     * @return ResponseEntity с опубликованным постом в формате PostViewDto.
     */
    @PutMapping("/{postId}/publish")
    public ResponseEntity<PostViewDto> publishPost(@PathVariable long postId) {
        log.info("Received request to publish post with ID: {}", postId);

        PostViewDto postViewDto = postService.publishPost(postId);

        log.info("Post published successfully: {}", postViewDto);
        return ResponseEntity.ok(postViewDto);
    }

    /**
     * Обновляет пост по его ID.
     *
     * @param postUpdateDto DTO с данными для обновления поста.
     * @param postId        ID поста для обновления.
     * @return ResponseEntity с обновленным постом в формате PostViewDto.
     */
    @PutMapping("/{postId}/update")
    public ResponseEntity<PostViewDto> updatePost(@Valid @RequestBody PostUpdateDto postUpdateDto,
                                                  @PathVariable long postId) {
        log.info("Received request to update post with ID: {}", postId);

        postValidator.validateAuthor(postUpdateDto, postId);
        PostViewDto postViewDto = postService.updatePost(postUpdateDto, postId);

        log.info("Post updated successfully: {}", postViewDto);
        return ResponseEntity.ok(postViewDto);
    }

    /**
     * Выполняет мягкое удаление поста по его ID.
     *
     * @param postId ID поста для мягкого удаления.
     * @return ResponseEntity с мягко удаленным постом в формате PostViewDto.
     */
    @PutMapping("/{postId}/soft-delete")
    public ResponseEntity<PostViewDto> softDeletePost(@PathVariable long postId) {
        log.info("Received request to soft delete post with ID: {}", postId);

        PostViewDto postViewDto = postService.softDeletePost(postId);

        log.info("Post soft-deleted successfully: {}", postViewDto);
        return ResponseEntity.ok(postViewDto);
    }

    /**
     * Получает пост по его ID.
     *
     * @param postId ID поста для получения.
     * @return ResponseEntity с найденным постом в формате PostViewDto.
     */
    @GetMapping("/{postId}")
    public ResponseEntity<PostViewDto> getPost(@PathVariable long postId) {
        log.info("Received request to get post with ID: {}", postId);

        PostViewDto postViewDto = postService.getPost(postId);

        log.info("Post get successfully: {}", postViewDto);
        return ResponseEntity.ok(postViewDto);

    }

    /**
     * Получает черновики постов для указанного пользователя.
     *
     * @param userId ID пользователя, для которого запрашиваются черновики.
     * @return ResponseEntity со списком черновиков в формате PostViewDto.
     */
    @GetMapping("/user/{userId}/draft")
    public ResponseEntity<List<PostViewDto>> getUserDraft(@PathVariable long userId) {
        log.info("Received request to get user draft post with ID: {}", userId);

        List<PostViewDto> postsViewDto = postService.getUserDraft(userId);

        log.info("Draft posts fetched successfully for user with ID: {}." +
                        " Number of posts: {}", userId, postsViewDto.size());
        return ResponseEntity.ok(postsViewDto);
    }

    /**
     * Получает черновики постов для указанного проекта.
     *
     * @param projectId ID проекта, для которого запрашиваются черновики.
     * @return ResponseEntity со списком черновиков в формате PostViewDto.
     */
    @GetMapping("/project/{projectId}/draft")
    public ResponseEntity<List<PostViewDto>> getProjectDraft(@PathVariable long projectId) {
        log.info("Received request to fetch draft posts for project with ID: {}", projectId);

        List<PostViewDto> postsViewDto = postService.getProjectDraft(projectId);

        log.info("Draft posts fetched successfully for project with ID:" +
                        " {}. Number of posts: {}", projectId, postsViewDto.size());
        return ResponseEntity.ok(postsViewDto);
    }

    /**
     * Получает опубликованные посты для указанного пользователя.
     *
     * @param userId ID пользователя, для которого запрашиваются опубликованные посты.
     * @return ResponseEntity со списком опубликованных постов в формате PostViewDto.
     */
    @GetMapping("/user/{userId}/published-post")
    public ResponseEntity<List<PostViewDto>> getAuthorPublishedPost(@PathVariable long userId) {
        log.info("Received request to fetch published posts for user with ID: {}", userId);

        List<PostViewDto> postsViewDto = postService.getAuthorPublishedPost(userId);

        log.info("Published posts fetched successfully for user with ID: {}." +
                " Number of posts: {}", userId, postsViewDto.size());
        return ResponseEntity.ok(postsViewDto);
    }

    /**
     * Получает опубликованные посты для указанного проекта.
     *
     * @param projectId ID проекта, для которого запрашиваются опубликованные посты.
     * @return ResponseEntity со списком опубликованных постов в формате PostViewDto.
     */
    @GetMapping("/project/{projectId}/published-post")
    public ResponseEntity<List<PostViewDto>> getProjectPublishedPost(@PathVariable long projectId) {
        log.info("Received request to fetch published posts for project with ID: {}", projectId);

        List<PostViewDto> postsViewDto = postService.getProjectPublishedPost(projectId);

        log.info("Published posts fetched successfully for project with ID: {}." +
                " Number of posts: {}", projectId, postsViewDto.size());
        return ResponseEntity.ok(postsViewDto);
    }
}
