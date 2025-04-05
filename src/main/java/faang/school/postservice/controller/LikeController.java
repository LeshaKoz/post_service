package faang.school.postservice.controller;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.model.ElementType;
import faang.school.postservice.service.LikeService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/likes")
public class LikeController {

    private final LikeService likeService;

    @Operation(summary = "Лайк на пост",
            description = "Позволяет пользователю поставить лайк на указанный пост")
    @PostMapping("/post")
    @ResponseStatus(HttpStatus.CREATED)
    public LikeDto userLikeThePost(@Valid @RequestBody LikeDto dto) {
        return likeService.userLike(dto, ElementType.POST);
    }

    @Operation(summary = "Удаление лайка с поста",
            description = "Позволяет пользователю удалить лайк с указанного поста")
    @DeleteMapping("/post/{likeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeLikeFromPost(@PathVariable @NotNull @Positive Long likeId,
                                   @Valid @RequestBody LikeDto dto) {
        likeService.removeLike(likeId, dto, ElementType.POST);
    }

    @Operation(summary = "Лайк на комментарий",
            description = "Позволяет пользователю поставить лайк на указанный комментарий")
    @PostMapping("/post/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public LikeDto userLikeTheComment(@Valid @RequestBody LikeDto dto) {
        return likeService.userLike(dto, ElementType.COMMENT);
    }

    @Operation(summary = "Удаление лайка с комментария",
            description = "Позволяет пользователю удалить лайк с указанного комментария")
    @DeleteMapping("/post/comment/{likeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeLikeFromComment(@PathVariable @NotNull @Positive Long likeId,
                                      @Valid @RequestBody LikeDto dto) {
        likeService.removeLike(likeId, dto, ElementType.COMMENT);
    }
}
