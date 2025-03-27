package faang.school.postservice.dto.file;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class FileMetaData {
    private byte[] data;
    private String originalName;
    private String type;
    private String extension;
}
