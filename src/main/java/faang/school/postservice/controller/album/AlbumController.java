package faang.school.postservice.controller.album;

import faang.school.postservice.dto.album.AlbumCreateUpdateDto;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.service.album.implementations.AlbumServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/albums")
@RequiredArgsConstructor
public class AlbumController {
    private final AlbumServiceImpl albumService;

    @PostMapping
    public ResponseEntity<AlbumDto> createAlbum(@RequestHeader("x-user-id") long userId,
                                                @RequestBody @Valid AlbumCreateUpdateDto createDto) {
        AlbumDto responseDto = albumService.createAlbum(createDto);
        URI albumUri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{albumId}")
                .buildAndExpand(responseDto.getId())
                .toUri();
        return ResponseEntity.created(albumUri).body(responseDto);
    }

    @PostMapping("/{albumId}/posts/{postId}")
    public ResponseEntity<AlbumDto> addPostToAlbum(@RequestHeader("x-user-id") long userId,
                                                   @PathVariable("albumId") long albumId,
                                                   @PathVariable("postId") long postId) {
        AlbumDto responseDto = albumService.addPostToAlbum(albumId, postId);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{albumId}/posts/{postId}")
    public ResponseEntity<Void> deletePostFromAlbum(@RequestHeader("x-user-id") long userId,
                                                    @PathVariable("albumId") long albumId,
                                                    @PathVariable("postId") long postId) {
        albumService.deletePostFromAlbum(albumId, postId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{albumId}/favorite")
    public ResponseEntity<Void> addAlbumToFavorites(@RequestHeader("x-user-id") long userId,
                                                    @PathVariable("albumId") long albumId) {
        albumService.addAlbumToFavorites(albumId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{albumId}/favorite")
    public ResponseEntity<Void> deleteAlbumFromFavorites(@RequestHeader("x-user-id") long userId,
                                                         @PathVariable("albumId") long albumId) {
        albumService.deleteAlbumFromFavorites(albumId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{albumId}")
    public ResponseEntity<AlbumDto> getAlbumById(@RequestHeader("x-user-id") long userId, @PathVariable("albumId") long albumId) {
        AlbumDto responseDto = albumService.getAlbumById(albumId);
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/filter")
    public ResponseEntity<List<AlbumDto>> getAllAlbums(@RequestHeader("x-user-id") long userId,
                                                       @RequestBody AlbumFilterDto filterDto) {
        List<AlbumDto> filteredAlbums = albumService.getAllAlbums(filterDto);
        return ResponseEntity.ok(filteredAlbums);
    }

    @PostMapping("/users/{authorId}/filter")
    public ResponseEntity<List<AlbumDto>> getUserAlbums(@RequestHeader("x-user-id") long userId,
                                                        @PathVariable("authorId") long authorId,
                                                        @RequestBody AlbumFilterDto filterDto) {
        List<AlbumDto> filteredAlbums = albumService.getUserAlbums(authorId, filterDto);
        return ResponseEntity.ok(filteredAlbums);
    }

    @PostMapping("/users/{authorId}/favorite/filter")
    public ResponseEntity<List<AlbumDto>> getUserFavoriteAlbums(@RequestHeader("x-user-id") long userId,
                                                                @PathVariable("authorId") long authorId,
                                                                @RequestBody AlbumFilterDto filterDto) {
        List<AlbumDto> filteredAlbums = albumService.getUserFavoriteAlbums(authorId, filterDto);
        return ResponseEntity.ok(filteredAlbums);
    }

    @PatchMapping("/{albumId}")
    public ResponseEntity<AlbumDto> updateAlbum(@RequestHeader("x-user-id") long userId,
                                                @RequestBody @Valid AlbumCreateUpdateDto updateDto,
                                                @PathVariable("albumId") long albumId) {
        AlbumDto responseDto = albumService.updateAlbum(albumId, updateDto);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{albumId}")
    public ResponseEntity<Void> deleteAlbum(@RequestHeader("x-user-id") long userId, @PathVariable("albumId") long albumId) {
        albumService.deleteAlbum(albumId);
        return ResponseEntity.noContent().build();
    }
}