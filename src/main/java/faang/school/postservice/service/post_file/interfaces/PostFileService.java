package faang.school.postservice.service.post_file.interfaces;

import faang.school.postservice.dto.file.FileMetaData;
import faang.school.postservice.dto.post_file.PostFileDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostFileService {
    void uploadFilesToPost(long postId, List<MultipartFile> files);

    List<PostFileDto> getPostFilesInfo(long postId);

    void deletePostFile(long postId, long fileId);

    FileMetaData downloadFile(long postId, long fileId);
}
