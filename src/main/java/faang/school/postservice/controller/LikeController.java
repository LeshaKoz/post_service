package faang.school.postservice.controller;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.LikeViewDto;
import faang.school.postservice.service.LikeService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер для работы с лайками постов и комментариев.
 * <p>
 * Основные методы:
 * <ul>
 *   <li>{@link #likePost(Long)} - добавляет лайк к указанному посту</li>
 *   <li>{@link #unlikePost(Long)} - удаляет лайк с указанного поста</li>
 *   <li>{@link #likeComment(Long)} - добавляет лайк к указанному комментарию</li>
 *   <li>{@link #unlikeComment(Long)} - удаляет лайк с указанного комментария</li>
 * </ul>
 * <p>
 * Все методы требуют аутентификации пользователя.
 *
 * @author gulnaz21
 */
@Slf4j
@RestController
@RequestMapping("/likes")
@RequiredArgsConstructor
@Validated
public class LikeController {
    private final LikeService likeService;
    private final UserContext userContext;

    /**
     * Добавляет лайк к посту.
     *
     * @param postId Идентификатор поста, к которому добавляется лайк.
     * @return Объект {@link LikeViewDto}, содержащий информацию о добавленном лайке.
     */
    @PostMapping("/posts/{postId}")
    public LikeViewDto likePost(@PathVariable
                                @NotNull Long postId) {
        long userId = userContext.getUserId();
        return likeService.likePost(postId, userId);
    }

    /**
     * Удаляет лайк с поста.
     *
     * @param postId Идентификатор поста, с которого удаляется лайк.
     */
    @DeleteMapping("/posts/{postId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unlikePost(@PathVariable
                           @NotNull Long postId) {
        long userId = userContext.getUserId();
        likeService.unlikePost(postId, userId);
    }

    /**
     * Добавляет лайк к комментарию.
     *
     * @param commentId Идентификатор комментария, к которому добавляется лайк.
     * @return Объект {@link LikeViewDto}, содержащий информацию о добавленном лайке.
     */
    @PostMapping("/comment/{commentId}")
    public LikeViewDto likeComment(@PathVariable
                                   @NotNull Long commentId) {
        long userId = userContext.getUserId();
        return likeService.likeComment(commentId, userId);
    }

    /**
     * Удаляет лайк с комментария.
     *
     * @param commentId Идентификатор комментария, с которого удаляется лайк.
     */
    @DeleteMapping("/comment/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unlikeComment(@PathVariable
                              @NotNull Long commentId) {
        long userId = userContext.getUserId();
        likeService.unlikeComment(commentId, userId);
    }
}
