package faang.school.postservice.controller.post;

import faang.school.postservice.dto.post.AlbumRequestDto;
import faang.school.postservice.dto.post.AlbumResponseDto;
import faang.school.postservice.dto.post.AlbumUsersDto;
import faang.school.postservice.enums.Visibility;
import faang.school.postservice.filter.album.AlbumFilterDto;
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

    @PostMapping
    public AlbumResponseDto createAlbum(@RequestBody AlbumRequestDto dto) {
        return albumService.createAlbum(dto);
    }

    @PostMapping("/{albumId}/posts/{postId}")
    public AlbumResponseDto addPostToAlbum(@PathVariable @Min(1) long postId,
                                           @PathVariable @Min(1) long albumId) {
        return albumService.addPostToAlbum(postId, albumId);
    }

    @DeleteMapping("/{albumId}/posts/{postId}")
    public void deletePostFromAlbum(@PathVariable @Min(1) long postId,
                                    @PathVariable @Min(1) long albumId) {
        albumService.deletePostFromAlbum(postId, albumId);
    }

    @PostMapping("/favourites/{albumId}")
    public void addAlbumToFavourites(@PathVariable long albumId) {
        albumService.addAlbumToFavourites(albumId);
    }

    @DeleteMapping("/favourites/{albumId}")
    public void deleteAlbumFromFavourites(@PathVariable @NotNull @Min(1) long albumId) {
        albumService.deleteAlbumFromFavorites(albumId);
    }

    @GetMapping("/own")
    public List<AlbumResponseDto> getAllOwnAlbums(AlbumFilterDto filters) {
        return albumService.getAllOwnAlbums(filters);
    }

    @GetMapping("/page/{pageNumber}/size/{pageSize}")
    public List<AlbumResponseDto> geAllAlbumsByFilters(@PathVariable @Min(0) int pageNumber,
                                                       @PathVariable @Min(1) int pageSize,
                                                       AlbumFilterDto filters) {
        return albumService.getAllAlbumsByFilters(pageNumber, pageSize, filters);
    }

    @GetMapping("/own/favorites")
    public List<AlbumResponseDto> getAllFavoritesOwnAlbums(AlbumFilterDto filters) {
        return albumService.getAllFavoritesOwnAlbums(filters);
    }

    @PutMapping("/{id}")
    public AlbumResponseDto updateAlbum(@PathVariable @NotNull @Min(1) long id,
                                        @RequestBody AlbumRequestDto dto) {
        return albumService.updateAlbum(id, dto);
    }

    @DeleteMapping("/{id}")
    public void deleteAlbumById(@PathVariable @NotNull @Min(1) long id) {
        albumService.deleteAlbumById(id);
    }

    @GetMapping("/{id}")
    public AlbumResponseDto findAlbumById(@PathVariable @NotNull @Min(1) long id) {
        return albumService.getAlbumById(id);
    }

    @GetMapping("/author/{authorId}")
    public List<AlbumResponseDto> findAlbumsByAuthorId(@PathVariable @NotNull @Min(1) long authorId) {
        return albumService.getAlbumsByAuthorId(authorId);
    }

    @PutMapping("/{id}/visibility/{visibility}")
    public void changeVisibilityAlbum(@PathVariable @NotNull @Min(1) long id,
                                      @PathVariable @NotNull Visibility visibility) {
        albumService.changeVisibilityAlbum(id, visibility);
    }

    @PutMapping("/{id}/add-users-for-access")
    public void addUsersForAccessAlbum(@PathVariable @NotNull @Min(1) long id,
                                       @RequestBody @Valid AlbumUsersDto albumUsersDto) {
        albumService.addUsersForAccessAlbum(id, albumUsersDto);
    }
}
