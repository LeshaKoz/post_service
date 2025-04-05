package faang.school.postservice.controller;

import faang.school.postservice.dto.ResourceDto;
import faang.school.postservice.dto.filter.PostFilterDto;
import faang.school.postservice.dto.post.CreatePostDto;
import faang.school.postservice.dto.post.ReadPostDto;
import faang.school.postservice.dto.post.UpdatePostDto;
import faang.school.postservice.service.PostResourceService;
import faang.school.postservice.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
@Log4j2
@Validated
public class PostController {

    private final PostService postService;
    private final PostResourceService postResourceService;

    @Operation(summary = "Создание нового поста", description = "Создает новый пост с заголовком, содержимым и автором")
    @PostMapping
    public ReadPostDto create(@Valid @NotNull @RequestBody CreatePostDto createPostDto) {
        return postService.create(createPostDto);
    }

    @Operation(summary = "Обновляет пост", description = "Обновляет существующий пост по указанному идентификатору")
    @PutMapping("/{postId}")
    public ReadPostDto update(@PathVariable long postId, @Valid @NotNull @RequestBody UpdatePostDto updatePostDto) {
        return postService.update(postId, updatePostDto);
    }

    @Operation(summary = "Удаление поста", description = "Удаляет пост по его идентификатору")
    @DeleteMapping("/{postId}")
    public ReadPostDto delete(@PathVariable long postId) {
        return postService.delete(postId);
    }

    @Operation(summary = "Публикация поста", description = "Помечает пост как опубликованный")
    @PostMapping("/{postId}/publishing")
    public ReadPostDto publishPost(@PathVariable long postId) {
        return postService.publish(postId);
    }

    @Operation(summary = "Получение поста", description = "Возвращает пост по его идентификатору")
    @GetMapping("/{postId}")
    public ReadPostDto getPost(@PathVariable long postId) {
        return postService.getPost(postId);
    }

    @Operation(summary = "Фильтрация постов", description = "Возвращает отфильтрованные посты на основе параметров")
    @GetMapping("/filter")
    public List<ReadPostDto> getFilteredPosts(
            @RequestParam(required = false) Long authorId,
            @RequestParam(required = false) Long projectId,
            @RequestParam Boolean isPublished) {

        PostFilterDto postFilterDto = new PostFilterDto(authorId, projectId, isPublished);
        return postService.getFilteredPosts(postFilterDto);
    }

    @Operation(summary = "Добавление изображения к посту",
            description = "Добавляет изображение в пост по его идентификатору")
    @PutMapping("/{id}/image")
    public ResponseEntity<ResourceDto> addImage(@PathVariable("id") @Min(1) Long postId,
                                                @RequestParam("image") MultipartFile image) {
        ResourceDto resourceDto = postResourceService.addPostImage(postId, image);
        return ResponseEntity.ok(resourceDto);
    }

    @Operation(summary = "Удаление изображения из поста", description = "Удаляет изображение из поста по ключу")
    @DeleteMapping("/{id}/image")
    public ResponseEntity<String> deleteImage(@PathVariable("id") @Min(1) Long postId,
                                              @RequestParam("key") @NotBlank String key) {
        String deletedKey = postResourceService.deleteImageByKey(postId, key);
        return ResponseEntity.ok(deletedKey);
    }

    @Operation(summary = "Получение изображения", description = "Возвращает изображение по ключу")
    @GetMapping("/image")
    public ResponseEntity<byte[]> getImage(@RequestParam("key") @NotBlank String key) {
        byte[] imageBytes = postResourceService.getImageByKey(key);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_JPEG_VALUE)
                .body(imageBytes);
    }

    @Operation(summary = "Получает все ключи изображений",
            description = "Возвращает список всех ключей изображений для поста")
    @GetMapping("/{id}/image/all")
    public ResponseEntity<List<String>> getAllImageKeys(@PathVariable("id") @Min(1) Long postId) {
        List<String> keys = postResourceService.getAllImageKeysByPostId(postId);
        return ResponseEntity.ok(keys);
    }

    @Operation(summary = "Удаляет все изображения из поста",
            description = "Удаляет все изображения, связанные с постом")
    @DeleteMapping("/{id}/image/all")
    public ResponseEntity<Void> deleteAllImages(@PathVariable("id") @Min(1) Long postId) {
        postResourceService.deleteAllImagesByPostId(postId);
        return ResponseEntity.ok().build();
    }
}