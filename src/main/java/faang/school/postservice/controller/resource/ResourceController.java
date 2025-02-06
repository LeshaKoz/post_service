package faang.school.postservice.controller.resource;


import faang.school.postservice.dto.resource.ResourceRequest;
import faang.school.postservice.dto.resource.ResourceResponse;
import faang.school.postservice.service.resource.ResourceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/resource")
public class ResourceController {
    private final ResourceService resourceService;

    @PostMapping("{postId}/addFiles")
    public List<ResourceResponse> addFilesToPost(@PathVariable long postId, @RequestBody List<MultipartFile> files) {
        return resourceService.addFilesToPost(postId, files);
    }

    @PostMapping("{postId}/addFile")
    public ResourceResponse addFileToPost(@PathVariable long postId, @RequestBody MultipartFile file) {
        return resourceService.addFileToPost(postId, file);
    }

/*    @DeleteMapping("/removeFile")
    public void removeFileFromPost(@RequestBody @Valid ResourceRequest resourceRequest) {
        resourceService.removeFileFromPost(resourceRequest);
    }*/

    @GetMapping("{postId}/getFiles")
    public List<byte[]> getFilesForPost(@PathVariable long postId) {

        List<InputStream> inputStreams = resourceService.getFilesForPost(postId);
        return inputStreams.stream()
                .map(input -> {
                    try {
                        byte[] imageBytes = input.readAllBytes();
                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.IMAGE_PNG);
                        return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();
    }
}

