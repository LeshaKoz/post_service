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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

import static faang.school.postservice.controller.album.AlbumConstant.*;

@RestController
@RequiredArgsConstructor
public class AlbumController {

    private final AlbumService albumService;

    @PostMapping(CREATE_ALBUM)
    public AlbumDto createAlbum(@RequestParam long userId, @RequestBody AlbumDto albumDto) {
        return albumService.createAlbum(userId, albumDto);
    }

    @GetMapping(ADD_POST)
    public AlbumDto addPost(@RequestParam long albumId, @RequestParam long userId, @RequestParam long postId) {
        return albumService.addPost(albumId, userId, postId);
    }

    @PostMapping(SHOW_ALL)
    public List<AlbumDto> showAllAlbums(@RequestBody(required = false) Optional<AlbumFilterDto> albumFilterDto) {
        return albumService.showAllAlbums(albumFilterDto);
    }

    @GetMapping(FIND_BY_ALBUM_ID)
    public AlbumDto findById(@PathVariable long albumId) {
        return albumService.findById(albumId);
    }

    @PostMapping(FIND_BY_AUTHOR_ID)
    public List<AlbumDto> findByAuthorId(@RequestParam long authorId, @RequestBody(required = false) Optional<AlbumFilterDto> albumFilterDto) {
        return albumService.findByAuthorId(authorId, albumFilterDto);
    }

    @GetMapping(FIND_POSTS)
    public List<PostDto> findByIdWithPosts(@PathVariable long albumId) {
        return albumService.findByIdWithPosts(albumId);
    }

    @GetMapping(ADD_ALBUM_TO_FAVORITE)
    public AlbumDto addAlbumToFavorites(@RequestParam long albumId, @RequestParam long userId) {
        return albumService.addAlbumToFavorites(albumId, userId);
    }

    @GetMapping(DELETE_ALBUM_FROM_FAVORITE)
    public AlbumDto deleteAlbumFromFavorites(@RequestParam long albumId, @RequestParam long userId) {
        return albumService.deleteAlbumFromFavorites(albumId, userId);
    }

    @PostMapping(FIND_FAVORITE_ALBUMS)
    public List<AlbumDto> findFavoriteAlbumsByUserId(@RequestParam long userId, @RequestBody(required = false) Optional<AlbumFilterDto> albumFilterDto) {
        return albumService.findFavoriteAlbumsByUserId(userId, albumFilterDto);
    }

    @GetMapping(DELETE_ALBUM_FROM_FAVORITE)
    public AlbumDto deleteAlbum(@RequestParam long albumId, @RequestParam long userId) {
        return albumService.deleteAlbum(albumId, userId);
    }

    @GetMapping(DELETE_POST)
    public AlbumDto deletePost(long albumId, long userId, long postId) {
        return albumService.deletePost(albumId, userId, postId);
    }

}
