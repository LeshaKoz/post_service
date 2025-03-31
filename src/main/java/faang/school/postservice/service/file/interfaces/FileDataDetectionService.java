package faang.school.postservice.service.file.interfaces;

import faang.school.postservice.dto.file.FileMetaData;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileDataDetectionService {
    FileMetaData detect(MultipartFile file) throws IOException;
}
