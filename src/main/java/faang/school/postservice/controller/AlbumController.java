package faang.school.postservice.controller;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.PostAlbumDto;
import faang.school.postservice.service.AlbumService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/albums")
@RequiredArgsConstructor
public class AlbumController {
    private final AlbumService albumService;

    @PostMapping
    public ResponseEntity<AlbumDto> createAlbum(@RequestBody AlbumDto albumDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(albumService.create(albumDto));
    }

    @PostMapping("/posts")
    public ResponseEntity<Void> addPostToAlbum(@RequestBody PostAlbumDto dto) {
        albumService.addPostToAlbum(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}