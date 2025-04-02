package faang.school.postservice.controller;

import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.service.PostResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/post/resources")
public class PostResourceController {
    private final PostResourceService postResourceService;

    @PutMapping("/{postId}")
    public @Validated ResourceDto addResource(@PathVariable Long postId,
                                              @RequestParam("files") List<MultipartFile> files) {
        ResourceDto resourceDto = postResourceService.add(postId, files);
        log.info("The file was added to the post");
        return resourceDto;
    }

    @DeleteMapping("/{postId}/{resourceId}")
    public void deleteResource(@PathVariable Long postId, @PathVariable Long resourceId) {
        postResourceService.delete(postId, resourceId);
    }
}
