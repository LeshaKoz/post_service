package faang.school.postservice.controller.resource;

import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.service.resource.ResourceService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/resource")
@RestController
public class ResourceController {
    private final ResourceService resourceService;

    @PostMapping("/post/{postId}")
    @ResponseStatus(HttpStatus.CREATED)
    public List<ResourceDto> attachImages(@PathVariable Long postId,
                                          @RequestParam("imageFiles") @NonNull List<MultipartFile> imageFiles) {
        return resourceService.attachImages(postId, imageFiles);
    }

    @DeleteMapping("/post/{resourceId}")
    public ResourceDto deleteImage(@PathVariable Long resourceId) {
        return resourceService.deleteImage(resourceId);
    }

    @PutMapping("/post/{resourceId}")
    public ResourceDto restoreImage(@PathVariable Long resourceId) {
        return resourceService.restoreImage(resourceId);
    }
}