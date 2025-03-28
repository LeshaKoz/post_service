package faang.school.postservice.service.amazons3.interfaces;

import com.amazonaws.services.s3.model.S3Object;
import faang.school.postservice.dto.file.FileMetaData;
import org.apache.commons.lang3.tuple.Pair;

import java.util.concurrent.CompletableFuture;

public interface AmazonS3Service {
    CompletableFuture<Pair<String, FileMetaData>> uploadFile(FileMetaData fileMetaData, String folder);

    void deleteFile(String fileKey);

    S3Object getFileFromS3(String fileKey);
}
