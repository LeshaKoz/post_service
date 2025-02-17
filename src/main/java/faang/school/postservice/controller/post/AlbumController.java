package faang.school.postservice.controller.post;

import faang.school.postservice.dto.post.AlbumResponseDto;
import faang.school.postservice.dto.post.AlbumUsersDto;
import faang.school.postservice.enums.Visibility;
import faang.school.postservice.service.post.AlbumService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${domain.path}/albums")
@Validated
public class AlbumController {

    private final AlbumService albumService;

    @GetMapping("/{id}")
    public AlbumResponseDto findAlbumById(@PathVariable @NotNull @Min(0) long id) {
        return albumService.getAlbumById(id);
    }

    @GetMapping("/author/{authorId}")
    public List<AlbumResponseDto> findAlbumsByAuthorId(@PathVariable @NotNull @Min(0) long authorId) {
        return albumService.getAlbumsByAuthorId(authorId);
    }

    @PutMapping("/{id}/visibility/{visibility}")
    public void changeVisibilityAlbum(@PathVariable @NotNull @Min(0) long id,
                                      @PathVariable @NotNull Visibility visibility) {
        albumService.changeVisibilityAlbum(id, visibility);
    }

    @PutMapping("/{id}/add-users-for-access")
    public void addUsersForAccessAlbum(@PathVariable @NotNull @Min(0) long id,
                                       @RequestBody @Valid AlbumUsersDto albumUsersDto) {
        albumService.addUsersForAccessAlbum(id, albumUsersDto);
    }
}
