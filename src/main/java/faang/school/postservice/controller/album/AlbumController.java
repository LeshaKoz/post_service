package faang.school.postservice.controller.album;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.album.*;
import faang.school.postservice.service.album.AlbumService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/albums")
@RequiredArgsConstructor
public class AlbumController {

    private final AlbumService albumService;
    private final UserContext userContext;

    @GetMapping
    public ResponseEntity<List<AlbumDto>> getAllAlbums(@Valid AlbumFilterDto filter) {
        return ResponseEntity.ok(albumService.getAllAlbums(filter));
    }

    @GetMapping("/{albumId}")
    public ResponseEntity<AlbumDto> getAlbum(@PathVariable @NotNull Long albumId) {
        return ResponseEntity.ok(albumService.getAlbumById(albumId));
    }

    @GetMapping("/user")
    public ResponseEntity<List<AlbumDto>> getUserAlbums(@Valid AlbumFilterDto filter) {
        Long userId = userContext.getUserId();
        return ResponseEntity.ok(albumService.getUserAlbums(userId, filter));
    }

    @GetMapping("/favorites")
    public ResponseEntity<List<AlbumDto>> getFavoriteAlbums(@Valid AlbumFilterDto filter) {
        Long userId = userContext.getUserId();
        return ResponseEntity.ok(albumService.getUserFavoriteAlbums(userId, filter));
    }

    @PostMapping
    public ResponseEntity<AlbumDto> createAlbum(@Valid @RequestBody CreateAlbumRequestDto request) {
        Long userId = userContext.getUserId();
        return ResponseEntity.ok(albumService.createAlbum(userId, request));
    }

    @PostMapping("/{albumId}/favorite")
    public ResponseEntity<Void> favoriteAlbum(@PathVariable @NotNull Long albumId) {
        Long userId = userContext.getUserId();
        albumService.addAlbumToFavorites(userId, albumId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{albumId}/posts/{postId}")
    public ResponseEntity<Void> addPostToAlbum(@PathVariable @NotNull Long albumId,
                                               @PathVariable @NotNull Long postId) {
        Long userId = userContext.getUserId();
        albumService.addPostToAlbum(userId, albumId, postId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{albumId}")
    public ResponseEntity<AlbumDto> updateAlbum(@PathVariable @NotNull Long albumId,
                                                @Valid @RequestBody UpdateAlbumRequestDto request) {
        Long userId = userContext.getUserId();
        return ResponseEntity.ok(albumService.updateAlbum(userId, albumId, request));
    }

    @DeleteMapping("/{albumId}")
    public ResponseEntity<Void> deleteAlbum(@PathVariable @NotNull Long albumId) {
        Long userId = userContext.getUserId();
        albumService.deleteAlbum(userId, albumId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{albumId}/favorite")
    public ResponseEntity<Void> removeFavoriteAlbum(@PathVariable @NotNull Long albumId) {
        Long userId = userContext.getUserId();
        albumService.removeAlbumFromFavorites(userId, albumId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{albumId}/posts/{postId}")
    public ResponseEntity<Void> removePostFromAlbum(@PathVariable @NotNull Long albumId,
                                                    @PathVariable @NotNull Long postId) {
        Long userId = userContext.getUserId();
        albumService.removePostFromAlbum(userId, albumId, postId);
        return ResponseEntity.ok().build();
    }
}
