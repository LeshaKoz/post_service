package faang.school.postservice.model;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

@Getter
@RequiredArgsConstructor
public class FileMultiPartFile implements MultipartFile {

    @NonNull
    private final String name;
    private final String originalFilename;
    private final String contentType;
    private final byte[] content;

    @Override
    public boolean isEmpty() {
        return content.length == 0;
    }

    @Override
    public long getSize() {
        return content.length;
    }

    @Override
    public byte @NonNull [] getBytes() {
        return content;
    }

    @NonNull
    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(content);
    }

    @Override
    public void transferTo(@NonNull File dest) throws IllegalStateException {
        throw new UnsupportedOperationException("Method transferTo is not implemented.");
    }
}
