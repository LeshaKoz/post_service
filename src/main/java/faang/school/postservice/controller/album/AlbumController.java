package faang.school.postservice.controller.album;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.dto.album.PostDto;
import faang.school.postservice.service.album.AlbumService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/album")
@RequiredArgsConstructor
public class AlbumController {

    private final AlbumService albumService;

    @PostMapping("/create_album")
    public AlbumDto createAlbum(@RequestParam long userId, @RequestBody AlbumDto albumDto) {
        return albumService.createAlbum(userId, albumDto);
    }

    @GetMapping("/add_post")
    public AlbumDto addPost(@RequestParam long albumId, @RequestParam long userId, @RequestParam long postId) {
        return albumService.addPost(albumId, userId, postId);
    }

    @PostMapping("/show_all")
    public List<AlbumDto> showAllAlbums(@RequestBody(required = false) Optional<AlbumFilterDto> albumFilterDto) {
        return albumService.showAllAlbums(albumFilterDto);
    }

    @GetMapping("/find_by_id/{albumId}")
    public AlbumDto findById(@PathVariable long albumId) {
        return albumService.findById(albumId);
    }

    @PostMapping("/find_by_author_id")
    public List<AlbumDto> findByAuthorId(@RequestParam long authorId, @RequestBody(required = false) Optional<AlbumFilterDto> albumFilterDto) {
        return albumService.findByAuthorId(authorId, albumFilterDto);
    }

    @GetMapping("/find_posts_by_album_id/{albumId}")
    public List<PostDto> findByIdWithPosts(@PathVariable long albumId) {
        return albumService.findByIdWithPosts(albumId);
    }

    @GetMapping("/add_album_to_favorites")
    public AlbumDto addAlbumToFavorites(@RequestParam long albumId, @RequestParam long userId) {
        return albumService.addAlbumToFavorites(albumId, userId);
    }

    @GetMapping("/delete_album_from_favorites")
    public AlbumDto deleteAlbumFromFavorites(@RequestParam long albumId, @RequestParam long userId) {
        return albumService.deleteAlbumFromFavorites(albumId, userId);
    }

    @PostMapping("/find_favorite_albums")
    public List<AlbumDto> findFavoriteAlbumsByUserId(@RequestParam long userId, @RequestBody(required = false) Optional<AlbumFilterDto> albumFilterDto) {
        return albumService.findFavoriteAlbumsByUserId(userId, albumFilterDto);
    }

    @GetMapping("/delete_album")
    public AlbumDto deleteAlbum(@RequestParam long albumId, @RequestParam long userId) {
        return albumService.deleteAlbum(albumId, userId);
    }

}
