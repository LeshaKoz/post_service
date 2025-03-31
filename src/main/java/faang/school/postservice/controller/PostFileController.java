package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostFileDto;
import faang.school.postservice.service.PostFileService;
import faang.school.postservice.service.file.FileData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/posts/resourses")
@RequiredArgsConstructor
public class PostFileController {

    private final PostFileService postFileService;

    @Operation(summary = "Upload files to post")
    @PostMapping(
            value = "/{postId}/files",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String uploadFilesToPost(
            @Parameter(
                    description = "Files to upload",
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            array = @ArraySchema(
                                    schema = @Schema(type = "string", format = "binary")
                            )
                    )
            )
            @RequestPart("files") @NotNull @NotEmpty List<@NotNull MultipartFile> files,
            @PathVariable @Min(1) long postId,
            @RequestHeader("X-User-Id") Long userId
    ) {
        postFileService.uploadFiles(files, postId);
        return "Upload started";
    }

    @GetMapping("/{postId}/files")
    public List<PostFileDto> getPostFilesInfo(@PathVariable @Min(1) long postId,
                                              @RequestHeader("X-User-Id") Long userId) {
        return postFileService.getPostFilesInfo(postId);
    }

    @DeleteMapping("/{postId}/files/{fileId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePostFile(
            @PathVariable @Min(1) long postId,
            @PathVariable @Min(1) long fileId,
            @RequestHeader("X-User-Id") Long userId
    ) {
        postFileService.deletePostFile(postId, fileId);
    }

    @GetMapping("/{postId}/files/{fileId}")
    public ResponseEntity<byte[]> downloadPostFile(
            @PathVariable @Min(1) long postId,
            @PathVariable @Min(1) long fileId,
            @RequestHeader("X-User-Id") Long userId
    ) {
        FileData fileData = postFileService.downloadFile(postId, fileId);
        HttpHeaders headers = new HttpHeaders();
        if (fileData.getType() != null) {
            headers.setContentType(MediaType.parseMediaType("%s/%s".formatted(fileData.getType(), fileData.getExtension())));
        } else {
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        }
        headers.setContentDisposition(ContentDisposition.attachment().filename(fileData.getOriginalName()).build());
        return new ResponseEntity<>(fileData.getData(), headers, HttpStatus.OK);
    }
}
