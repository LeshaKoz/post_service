package faang.school.postservice.controller.album;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.service.album.AlbumService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/albums")
@RequiredArgsConstructor
public class AlbumController {

    private final AlbumService albumService;

    @PostMapping("/new")
    public ResponseEntity<AlbumDto> createAlbum(
            @RequestHeader("userId") long userId,
            @RequestBody AlbumDto albumDto) {
        AlbumDto savedDto = albumService.createAlbum(userId, albumDto);
        return new ResponseEntity<>(savedDto, HttpStatus.CREATED);
    }

    @PostMapping("/{albumId}")
    public ResponseEntity<AlbumDto> addPostToAlbum(
            @RequestHeader("userId") Long userId,
            @RequestParam("postId") Long postId,
            @PathVariable Long albumId) {
        AlbumDto savedDto = albumService.addPostToAlbum(userId, postId, albumId);
        return new ResponseEntity<>(savedDto, HttpStatus.OK);
    }

    @DeleteMapping("/{albumId}/{postId}")
    public ResponseEntity<AlbumDto> deletePostFromAlbum(
            @RequestHeader("userId") Long userId,
            @PathVariable Long postId,
            @PathVariable Long albumId) {
        AlbumDto updatedAlbum = albumService.deletePostFromAlbum(userId, postId, albumId);
        return new ResponseEntity<>(updatedAlbum, HttpStatus.OK);
    }

    @GetMapping("/{albumId}")
    public ResponseEntity<AlbumDto> getAlbumById(
            @PathVariable long albumId) {
        AlbumDto albumDto = albumService.getAlbumById(albumId);
        return new ResponseEntity<>(albumDto, HttpStatus.OK);
    }

    @PutMapping("/{albumId}")
    public ResponseEntity<AlbumDto> updateAlbum(
            @RequestHeader("userId") Long userId,
            @RequestBody AlbumDto albumDto) {
        AlbumDto updatedAlbum = albumService.updateAlbum(userId, albumDto);
        return new ResponseEntity<>(updatedAlbum, HttpStatus.OK);
    }

    @DeleteMapping("/{albumId}")
    public ResponseEntity<AlbumDto> deleteAlbum(
            @RequestHeader("userId") Long userId,
            @PathVariable Long albumId) {
        albumService.deleteAlbum(userId, albumId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/{userId}")
    public ResponseEntity<List<AlbumDto>> getAllAlbumsByAuthorIdWithFilters(
            @PathVariable Long userId,
            @RequestBody AlbumFilterDto albumFilterDto) {
        List<AlbumDto> albums = albumService.getAllAlbumsByAuthorIdWithFilters(userId, albumFilterDto);
        return ResponseEntity.ok(albums);
    }

    @PostMapping("/all-albums")
    public ResponseEntity<List<AlbumDto>> getAllAlbumsWithFilters(
            @RequestBody AlbumFilterDto albumFilterDto) {
        List<AlbumDto> albums = albumService.getAllAlbumsWithFilters(albumFilterDto);
        return ResponseEntity.ok(albums);
    }

}
