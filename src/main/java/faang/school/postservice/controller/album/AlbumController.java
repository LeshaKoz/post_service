package faang.school.postservice.controller.album;

import faang.school.postservice.dto.album.AlbumCreateDto;
import faang.school.postservice.dto.album.AlbumEditDto;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.dto.album.AlbumReadDto;
import faang.school.postservice.service.album.AlbumService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RequestMapping("/albums")
@RestController
@RequiredArgsConstructor
public class AlbumController {
    private final AlbumService albumService;

    @Operation(summary = "Создание альбома",
            description = "Позволяет создать новый альбом")
    @PostMapping
    public AlbumReadDto createAlbum(@Valid @RequestBody AlbumCreateDto albumCreateDto) {
        return albumService.createAlbum(albumCreateDto);
    }

    @Operation(summary = "Добавление альбома в избранное",
            description = "Позволяет добавить альбом в список избранных")
    @PostMapping("/favorite/{albumId}")
    public AlbumReadDto addAlbumToFavorite(@Valid @Positive @PathVariable long albumId) {
        return albumService.addAlbumToFavorites(albumId);
    }

    @Operation(summary = "Удаление альбоа из избранного",
            description = "Позволяет удалить альбом из списка избранных")
    @DeleteMapping("/favorite/{albumId}")
    public AlbumReadDto deleteAlbumFromFavorite(@Valid @Positive @PathVariable long albumId) {
        return albumService.deleteAlbumFromFavorites(albumId);
    }

    @Operation(summary = "Найти альбом по ID",
            description = "Возвращает данные альбома по его идентификатору")
    @GetMapping("/{albumId}")
    public AlbumReadDto findAlbumById(@Valid @Positive @PathVariable long albumId) {
        return albumService.findAlbumById(albumId);
    }

    @Operation(summary = "Фильтрует альбомы автора",
            description = "Возвращает альбомы автора, отфильтрованные по заданным параметрам")
    @GetMapping("/filtered/{authorId}")
    public List<AlbumReadDto> findAuthorAlbumsByFilters(@Valid @Positive @PathVariable long authorId,
                                                        @Valid @RequestParam(required = false) String titlePattern,
                                                        @Valid @RequestParam(required = false) LocalDate fromDate) {
        return albumService.findAuthorAlbumsByFilters(AlbumFilterDto.builder()
                .titlePattern(titlePattern)
                .fromDate(fromDate).build(), authorId);
    }

    @Operation(summary = "Фильтрует все альбомы",
            description = "Возвращает все альбомы, отфильтрованные по заданным параметрам")
    @GetMapping("/filtered")
    public List<AlbumReadDto> findAllAlbumsByFilters(@Valid @RequestParam(required = false) String titlePattern,
                                                     @Valid @RequestParam(required = false) LocalDate fromDate) {
        return albumService.findAllAlbumsByFilters(AlbumFilterDto.builder()
                .titlePattern(titlePattern)
                .fromDate(fromDate).build());
    }

    @Operation(summary = "Фильтрует избранные альбомы",
            description = "Возвращает избранные альбомы, отфильтрованные по заданным параметрам")
    @GetMapping("/favorite/filtered")
    public List<AlbumReadDto> findFavoriteAlbumsByFilters(@Valid @RequestParam(required = false) String titlePattern,
                                                          @Valid @RequestParam(required = false) LocalDate fromDate) {
        return albumService.findFavoriteAlbumsByFilters(AlbumFilterDto.builder()
                .titlePattern(titlePattern)
                .fromDate(fromDate).build());
    }

    @Operation(summary = "Редактирует альбом",
            description = "Позволяет изменить данные альбома")
    @PutMapping
    public AlbumReadDto editAlbum(@Valid @RequestBody AlbumEditDto albumEditDto) {
        return albumService.editAlbum(albumEditDto);
    }

    @Operation(summary = "Удаляет альбом",
            description = "Позволяет удалить данные альбома")
    @DeleteMapping("/{albumId}")
    public void deleteAlbum(@Valid @Positive @PathVariable long albumId) {
        albumService.deleteAlbum(albumId);
    }
}
