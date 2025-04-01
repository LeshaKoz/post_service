package faang.school.postservice.service.validator;

import faang.school.postservice.exception.UploadFileException;
import faang.school.postservice.model.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FileValidator {
    @Value("${app.posts.files.max-file-size}")
    private long maxFileSize;
    @Value("${app.posts.files.max-post-files-count}")
    private int maxPostFiles;

    public void validateFiles(Post post, MultipartFile[] files) {
        validateFilesContentType(files);
        validateFilesCount(post, files);
        validateEachFileSize(files);
    }

    private void validateFilesContentType(MultipartFile[] files) {
        List<MultipartFile> list = Arrays.stream(files)
                .filter(file -> file.getContentType().contains("image"))
                .toList();
        if (files.length != list.size()) {
            throw new UploadFileException("Unsupported contentType. Not an image");
        }
    }

    private void validateEachFileSize(MultipartFile[] files) {
        Arrays.stream(files)
                .filter(file -> file.getSize() > maxFileSize)
                .findFirst()
                .ifPresent((file) -> {
                    throw new UploadFileException(String.format(
                            "The file [%s] size [%d] exceeds the allowed size [%d]",
                            file.getName(), file.getSize(), maxFileSize));
                });
    }

    private void validateFilesCount(Post post, MultipartFile[] files) {
        int totalFilesCount = post.getResources().size() + files.length;
        if (totalFilesCount > maxPostFiles) {
            throw new UploadFileException(
                    String.format("Number of files exceeded. Each post can contain no more than %d files. " +
                                    "However, the post already contains %d files, and you're trying to add %d more.",
                            maxPostFiles, post.getResources().size(), files.length));
        }
    }
}
