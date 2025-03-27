package faang.school.postservice.validator.post_file;

import faang.school.postservice.dto.file.FileMetaData;
import faang.school.postservice.dto.post_file.PostFileUploadRequirements;
import faang.school.postservice.dto.post_file.PostFileUploadRequirements.FileTypoInfo;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.FileProcessException;
import faang.school.postservice.exception.ForbiddenException;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.file.FileDataDetectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class PostFileValidator {
    private final PostFileUploadRequirements postFileUploadRequirements;
    private final FileDataDetectionService fileDataDetectionService;

    public void validateUploadFilesAmount(List<MultipartFile> files) {
        int minFileUploadAmount = postFileUploadRequirements.getMinAmount();
        int maxFileUploadAmount = postFileUploadRequirements.getMaxAmount();
        if (files.size() < minFileUploadAmount || files.size() > maxFileUploadAmount) {
            throw new DataValidationException(("Unable to upload to post more than %d or less than %d files. " +
                    "You are trying to upload %d files.")
                    .formatted(maxFileUploadAmount, minFileUploadAmount, files.size()));
        }
    }

    public void validateAlreadyUploadedFilesAmount(List<MultipartFile> files, int alreadyUploadedFilesAmount) {
        int maxFileUploadAmount = postFileUploadRequirements.getMaxAmount();
        if (alreadyUploadedFilesAmount + files.size() > maxFileUploadAmount) {
            throw new DataValidationException(("Unable to upload to post more than %d files. " +
                    "You are trying to upload %d files, already uploaded %d files.")
                    .formatted(maxFileUploadAmount, files.size(), alreadyUploadedFilesAmount));
        }
    }

    public void validatePostBelongsToUser(Post post, long userId) {
        if (post.getAuthorId() != userId) {
            throw new ForbiddenException(userId, "upload files to post with id %d".formatted(post.getId()));
        }
    }

    public void validateFilesNotEmpty(List<MultipartFile> files) {
        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                throw new DataValidationException("The file named '%s' is empty".formatted(file));
            }
        }
    }

    public List<FileMetaData> validateAndExtractFileMetadatas(List<MultipartFile> files) {
        List<FileMetaData> fileMetaDatas = new ArrayList<>();
        for (MultipartFile file : files) {
            try {
                FileMetaData fileMetaData = fileDataDetectionService.detect(file);
                validateFileExtension(fileMetaData);
                validateFileSize(fileMetaData);
                fileMetaDatas.add(fileMetaData);
            } catch (IOException e) {
                throw new FileProcessException("The file named '%s' is unable to be processed."
                        .formatted(file.getOriginalFilename()), e);
            }
        }
        return fileMetaDatas;
    }

    private void validateFileExtension(FileMetaData fileMetaData) {
        Map<String, FileTypoInfo> infoByFileType
                = postFileUploadRequirements.getInfoByFileType();
        FileTypoInfo fileTypoInfo = infoByFileType.getOrDefault(fileMetaData.getType(), infoByFileType.get("another"));
        Set<String> allowedExtensions = fileTypoInfo.getAllowedExtensions();
        System.out.println(fileMetaData.getData().length);
        if (!allowedExtensions.contains(fileMetaData.getExtension())) {
            throw new DataValidationException
                    (String.format("The file named '%s' is not allowed to be uploaded due to extension. " +
                            "Allowed extensions: %s.", fileMetaData.getOriginalName(), allowedExtensions));
        }
    }

    private void validateFileSize(FileMetaData fileMetaData) {
        Map<String, FileTypoInfo> infoByFileType
                = postFileUploadRequirements.getInfoByFileType();
        FileTypoInfo fileTypoInfo = infoByFileType.getOrDefault(fileMetaData.getType(), infoByFileType.get("another"));
        int fileTypeMaxSize = fileTypoInfo.getMaxSize();
        if (fileMetaData.getData().length > fileTypeMaxSize) {
            String sizeExceedMessage
                    = String.format("The file named '%s' with type '%s' exceeds the maximum allowed size. " +
                            "File size: %d bytes, allowed: %d bytes.",
                    fileMetaData.getOriginalName(),
                    fileMetaData.getType(),
                    fileMetaData.getData().length,
                    fileTypeMaxSize
            );
            throw new DataValidationException(sizeExceedMessage);
        }
    }
}
