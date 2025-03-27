package faang.school.postservice.controller.comment;

import faang.school.postservice.dto.file.FileMetaData;
import faang.school.postservice.dto.post_file.PostFileDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.post.interfaces.PostService;
import faang.school.postservice.service.post_file.interfaces.PostFileService;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final PostFileService postFileService;

    @GetMapping("/{postId}")
    public ResponseEntity<Post> getPostById(@PathVariable long postId) {
        Post postById = postService.getPostById(postId);
        return ok(postById);
    }

    @PostMapping("/{postId}/files")
    public ResponseEntity<Void> uploadFilesToPost(@PathVariable @Min(1) long postId,
                                                  @RequestPart @NotNull @NotEmpty List<@NotNull MultipartFile> files) {
        postFileService.uploadFilesToPost(postId, files);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/{postId}/files")
    public ResponseEntity<List<PostFileDto>> getPostFilesInfo(@PathVariable @Min(1) long postId) {
        List<PostFileDto> postFilesInfo = postFileService.getPostFilesInfo(postId);
        return ResponseEntity.ok(postFilesInfo);
    }

    @DeleteMapping("/{postId}/files/{fileId}")
    public ResponseEntity<Void> deletePostFile(@PathVariable @Min(1) long postId, @PathVariable @Min(1) long fileId) {
        postFileService.deletePostFile(postId, fileId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{postId}/files/{fileId}")
    public ResponseEntity<byte[]> downLoadPostFile(@PathVariable @Min(1) long postId,
                                                   @PathVariable @Min(1) long fileId) {
        FileMetaData fileMetaData = postFileService.downloadFile(postId, fileId);
        HttpHeaders headers = new HttpHeaders();
        if (fileMetaData.getType() != null) {
            headers.setContentType(MediaType.parseMediaType("%s/%s"
                    .formatted(fileMetaData.getType(), fileMetaData.getExtension())));
        } else {
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        }
        headers.setContentDisposition(ContentDisposition.attachment().filename(fileMetaData.getOriginalName()).build());
        return new ResponseEntity<>(fileMetaData.getData(), headers, HttpStatus.OK);
    }
}
