package faang.school.postservice.controller;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.model.ElementType;
import faang.school.postservice.service.LikeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/likes")
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/post")
    @ResponseStatus(HttpStatus.CREATED)
    public LikeDto userLikeThePost(@Valid @RequestBody LikeDto dto) {
        return likeService.userLike(dto, ElementType.POST);
    }

    @DeleteMapping("/post/{likeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeLikeFromPost(@PathVariable @NotNull @Positive Long likeId,
                                   @Valid @RequestBody LikeDto dto) {
        likeService.removeLike(likeId, dto, ElementType.POST);
    }

    @PostMapping("/post/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public LikeDto userLikeTheComment(@Valid @RequestBody LikeDto dto) {
        return likeService.userLike(dto, ElementType.COMMENT);
    }

    @DeleteMapping("/post/comment/{likeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeLikeFromComment(@PathVariable @NotNull @Positive Long likeId,
                                      @Valid @RequestBody LikeDto dto) {
        likeService.removeLike(likeId, dto, ElementType.COMMENT);
    }
}
